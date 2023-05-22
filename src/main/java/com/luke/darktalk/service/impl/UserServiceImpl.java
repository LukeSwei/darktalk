package com.luke.darktalk.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.contant.RedisConstant;
import com.luke.darktalk.contant.UserConstant;
import com.luke.darktalk.mapper.UserMapper;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.model.request.UserForgetDto;
import com.luke.darktalk.model.request.UserRegisterRequest;
import com.luke.darktalk.service.UserService;
import com.luke.darktalk.utils.AlgorithmUtils;
import com.luke.darktalk.utils.JsonUtil;
import com.luke.darktalk.utils.JwtUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.luke.darktalk.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = UserConstant.PASSWORD_SALT;

    /**
     * 修改密码方式1-验证码
     */
    private static final String CODE_CHANGE = "1";

    /**
     * 修改密码方式0-密码修改
     */
    private static final String OLD_PASSWORD_CHANGE = "0";

    @Override
    public BaseResponse userRegister(UserRegisterRequest register) {
        String userAccount = register.getUserAccount();
        String userPassword = register.getUserPassword();
        String checkPassword = register.getCheckPassword();
        String verifyCode = register.getVerifyCode();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if(StringUtils.isEmpty(verifyCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码未填写");
        }
        //校验验证码
        Object redisCode = redisTemplate.opsForValue().get(RedisConstant.LOGIN_VERIFY_CODE + userAccount);
        if(Objects.isNull(redisCode)){
            return new BaseResponse(4000,"","验证码已过期");
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码已过期请重新获取");
        }
        if(!verifyCode.equals(String.valueOf(redisCode))){
            return new BaseResponse(4000,"","验证码错误，请重新输入");
        }
        if (!userPassword.equals(checkPassword)) {
            return new BaseResponse(4000,"","两次输入密码不一致，请重新输入");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return new BaseResponse(4000,"","该账户已存在，请直接登录");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        String avaUrl = "https://api.multiavatar.com/"+ RandomUtil.randomString("dbiabda",2) +".png";
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername("user_"+ DateUtil.millisecond(new Date()));
        user.setAvatarUrl(avaUrl);
        user.setEmail(userAccount);
        boolean saveResult = this.save(user);
        Boolean delete = redisTemplate.delete(RedisConstant.LOGIN_VERIFY_CODE + userAccount);
        if (!saveResult) {
            return new BaseResponse(4000,"","注册失败");
        }
        return new BaseResponse(0,"","注册成功");
    }

    @Override
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
//        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        String userStr = JsonUtil.objToStr(safetyUser);
        String token = JwtUtils.generateJwtToken(userStr);
        redisTemplate.opsForHash().put(RedisConstant.LOGIN_TOKEN,safetyUser.getUserAccount(),safetyUser);
//        response.setHeader(UserConstant.USER_LOGIN_HEADER_TOKEN,token);

        return token;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setIntroduction(originUser.getIntroduction());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //拼接and查询
        for (String item : tagList) {
            wrapper = wrapper.like("tags", item);
        }
        List<User> users = userMapper.selectList(wrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Integer updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.getById(user.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 3. 触发更新
        return this.baseMapper.updateById(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String tokenHeader = request.getHeader(UserConstant.USER_LOGIN_HEADER_TOKEN);
        if(StringUtils.isEmpty(tokenHeader)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User user = JwtUtils.getUserByJwtToken(tokenHeader);
        return user;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        //Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        String tokenHeader = request.getHeader(UserConstant.USER_LOGIN_HEADER_TOKEN);
        User user = JwtUtils.getUserByJwtToken(tokenHeader);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;

    }

    /**
     * 修改密码
     *
     * @param dto dto
     * @return {@link BaseResponse}
     */
    @Override
    public BaseResponse changePwd(UserForgetDto dto) {
        String newPwd = dto.getNewPwd();
        String flag = dto.getFlag();
        if(CODE_CHANGE.equals(flag)){
            String verifyCode = dto.getVerifyCode();
            String codeKey = String.format(RedisConstant.LOGIN_VERIFY_CODE + dto.getUserAccount());
            String reCode = (String) redisTemplate.opsForValue().get(codeKey);
            //判断验证码正确并且过期
            if(StringUtils.isBlank(reCode)||!reCode.equals(verifyCode)){
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"验证码不正确或过期");
            }
        }else {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("userAccount",dto.getUserAccount());
            User user = this.getOne(wrapper);
            if(user == null){
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"用户不存在！");
            }
            String oldPassword = dto.getOldPassword();
            String oldEncrypt = DigestUtils.md5DigestAsHex((UserConstant.PASSWORD_SALT + oldPassword).getBytes());
            if(!oldEncrypt.equals(user.getUserPassword())){
                return ResultUtils.error(ErrorCode.PARAMS_ERROR,"原密码输入错误！");
            }
        }
        //加盐值修改密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.PASSWORD_SALT + newPwd).getBytes());
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(dto.getUserAccount()),"userAccount",dto.getUserAccount());
        wrapper.set("userPassword",encryptPassword);
        boolean update = this.update(wrapper);
        if(!update){
            return  ResultUtils.error(ErrorCode.SYSTEM_ERROR,"修改密码失败！");
        }
        return ResultUtils.success("密码修改成功");
    }

}




