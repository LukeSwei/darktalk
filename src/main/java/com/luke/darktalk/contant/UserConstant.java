package com.luke.darktalk.contant;

/**
 * 用户常量
 *
 * @author yupi
 */
public interface UserConstant {

    String USER_LOGIN_HEADER_TOKEN = "Authorization";

    String PASSWORD_SALT = "luke_jzxy";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    //  ------- 权限 --------

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 用户状态正常
     */
    int USER_STATUS_NORMAL=0;

    /**
     * 用户状态否认
     */
    int USER_STATUS_DENIED = 1;

    /**
     * 未删除
     */
    int IS_DELETE_FALSE = 0;

    /**
     * 已删除
     */
    int IS_DELETE_TRUE = 1;

}
