package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注
 *
 * @author caolu
 * @TableName follow
 * @date 2023/05/07
 */
@TableName(value = "follow")
@Data
public class Follow implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    @TableField(value = "userId")
    private Long userId;

    /**
     * 标签编号
     */
    @TableField(value = "followId")
    private Long followId;

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
    private String updateBy;

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

