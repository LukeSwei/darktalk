package com.luke.darktalk.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.component.mail.Mail;
import com.luke.darktalk.contant.RedisConstant;
import com.luke.darktalk.contant.UserConstant;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.request.UserForgetDto;
import com.luke.darktalk.model.request.UserLoginRequest;
import com.luke.darktalk.model.request.UserRegisterRequest;
import com.luke.darktalk.component.service.ProduceService;
import com.luke.darktalk.service.UserService;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author caolu
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户接口",value = "用户接口")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private ProduceService produceService;

    @Resource
    private RedisTemplate redisTemplate;


    @PostMapping("/forgetPwd")
    public BaseResponse forgetPwd(@RequestBody UserForgetDto dto){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userAccount",dto.getUserAccount());
        wrapper.eq("userStatus",UserConstant.USER_STATUS_NORMAL);
        wrapper.eq("isDelete",UserConstant.IS_DELETE_FALSE);
        User user = userService.getOne(wrapper);
        if(!Objects.isNull(user)){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"该用户未注册，请注册");
        }
        String code = RandomUtil.randomNumbers(6);
        Mail mail = new Mail();
        mail.setTo(user.getUserAccount());
        mail.setContent("您修改密码验证码为："+code+",请尽快使用，5分钟内有效");
        mail.setTitle("注册验证码");
        produceService.send(mail);
        String codeKey = String.format(RedisConstant.LOGIN_VERIFY_CODE+user.getUserAccount());
        redisTemplate.opsForValue().set(codeKey,code,300000,TimeUnit.MILLISECONDS);
        return ResultUtils.success("","验证码获取成功");
    }

    @PostMapping("/changePwd")
    @ApiOperation(value = "修改密码接口")
    public BaseResponse changePwd(@RequestBody UserForgetDto dto){
        return userService.changePwd(dto);
    }


    @PostMapping("/verify")
    public BaseResponse<String> verifyCode(@RequestBody UserRegisterRequest user){
        if(user==null&&StringUtils.isEmpty(user.getUserAccount())){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"账户为空");
        }
        String code = RandomUtil.randomNumbers(6);
        Mail mail = new Mail();
        mail.setTo(user.getUserAccount());
        mail.setContent("您的注册验证码为："+code+",请尽快使用，1分钟内有效");
        mail.setTitle("注册验证码");
        produceService.send(mail);
        String codeKey = String.format(RedisConstant.LOGIN_VERIFY_CODE+user.getUserAccount());
        redisTemplate.opsForValue().set(codeKey,code,300000,TimeUnit.MILLISECONDS);
        return ResultUtils.success("","验证码获取成功");
    }

    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String verifyCode = userRegisterRequest.getVerifyCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,verifyCode)) {
            return null;
        }
        BaseResponse result = userService.userRegister(userRegisterRequest);
        return result;
    }

    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String token = userService.userLogin(userAccount, userPassword, request,response);
        return ResultUtils.success(token);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        String header = request.getHeader(UserConstant.USER_LOGIN_HEADER_TOKEN);
        User currentUser = JwtUtils.getUserByJwtToken(header);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        String header = request.getHeader(UserConstant.USER_LOGIN_HEADER_TOKEN);
        if (!JwtUtils.checkToken(header)) {
            return false;
        }
        User user = JwtUtils.getUserByJwtToken(header);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(users);
    }

    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommend(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format(RedisConstant.REDIS_USER_RECOMMEND + loginUser.getId());
        Page<User> userPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage.getRecords());
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), wrapper);
        try {
            redisTemplate.opsForValue().set(redisKey, userPage, 1800000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set error", e);
        }
        return ResultUtils.success(userPage.getRecords());
    }

    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        Integer user1 = userService.updateUser(user, loginUser);
        return ResultUtils.success(user1);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }


}
