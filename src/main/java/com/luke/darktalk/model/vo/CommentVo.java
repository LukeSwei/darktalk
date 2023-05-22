package com.luke.darktalk.model.vo;

import lombok.Data;

@Data
public class CommentVo {

    private String commentContent;

    private Long momentId;

    private Long toUserId;

    private Long rootParentId;
}
