package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.luke.darktalk.model.vo.UserCommentVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签
 * @TableName comment
 */
@TableName(value ="comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 帖子id
     */
    @TableField(value = "momentId")
    private Long momentId;

    /**
     * 评论内容
     */
    @TableField(value = "commentContent")
    private String commentContent;

    /**
     * 父级评论id
     */
    @TableField(value = "toUserId")
    private Long toUserId;

    /**
     * 根评论ID
     */
    @TableField(value = "rootParentId")
    private Long rootParentId;

    /**
     * 点赞数量
     */
    @TableField(value = "likeNum")
    private Integer likeNum;

    /**
     * 创建人
     */
    @TableField(value = "createBy")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新人
     */
    @TableField(value = "updateBy")
    private Date updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private UserCommentVo userCommentVo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}