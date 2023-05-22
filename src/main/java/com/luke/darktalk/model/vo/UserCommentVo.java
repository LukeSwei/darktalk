package com.luke.darktalk.model.vo;

import lombok.Data;

/**
 * 用户评论视图显示
 *
 * @author caolu
 * @date 2023/05/15
 */
@Data
public class UserCommentVo {

    private String avatarUrl;

    private String username;

    private Long toUserId;

    private String toUserName;
}
