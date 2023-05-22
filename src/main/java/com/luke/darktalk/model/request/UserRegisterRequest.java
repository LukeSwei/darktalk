package com.luke.darktalk.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author yupi
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户帐户
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 检查密码
     */
    private String checkPassword;


    /**
     * 性别
     */
    private Integer gender;

    /**
     * 标签
     */
    private String tags;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 验证码
     */
    private String verifyCode;
}
