package com.luke.darktalk.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 忘记密码
 *
 * @author caolu
 * @date 2023/04/24
 */
@Data
public class UserForgetDto implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String newPwd;

    @ApiModelProperty(notes = "状态为1时输入此字段")
    private String verifyCode;

    @ApiModelProperty(value="1-验证码修改密码，0-原本密码修改")
    private String flag;

    @ApiModelProperty(value = "状态为0时需要输入此字段")
    private String oldPassword;
}
