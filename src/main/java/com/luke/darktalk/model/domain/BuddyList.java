package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系表
 * @TableName buddy_list
 */
@TableName(value ="buddy_list")
@Data
public class BuddyList implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户A的id
     */
    @TableField(value = "mineId")
    private Integer mineId;

    /**
     * 用户B的id
     */
    @TableField(value = "friendId")
    private Integer friendId;

    /**
     * 好友名称
     */
    @TableField(value = "friendName")
    private String friendName;

    /**
     * 好友头像连接
     */
    @TableField(value = "friendAvatarUrl")
    private Integer friendAvatarUrl;

    /**
     * 备注
     */
    @TableField(value = "noteName")
    private Integer noteName;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}