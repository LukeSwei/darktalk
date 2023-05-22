package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 朋友圈图片表
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 朋友圈id
     */
    @TableField(value = "momentId")
    private long momentId;

    /**
     * 图片连接
     */
    @TableField(value = "pictureUrl")
    private String pictureUrl;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private Long userId;

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
    private static final long serialVersionUID = 1L;
}