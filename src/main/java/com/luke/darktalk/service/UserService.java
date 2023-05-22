package com.luke.darktalk.service;

import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.darktalk.model.request.UserForgetDto;
import com.luke.darktalk.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户服务
 *
 * @author yupi
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest   用户账户
     * @return 新用户 id
     */
    BaseResponse userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @param response
     * @return 脱敏后的用户信息
     */
    String userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    List<User> searchUserByTags(List<String> tagList);

    Integer updateUser(User user, User loginUser);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    List<User> matchUsers(long num, User user);

    BaseResponse changePwd(UserForgetDto dto);
}
