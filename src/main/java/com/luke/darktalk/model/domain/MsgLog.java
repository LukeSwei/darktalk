package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消息投递日志
 * @TableName msg_log
 */
@TableName(value ="msg_log")
@Data
public class MsgLog implements Serializable {
    /**
     * 消息唯一标识
     */
    @TableId(value = "msg_id")
    private String msgId;

    /**
     * 消息体, json格式化
     */
    @TableField(value = "msg")
    private String msg;

    /**
     * 交换机
     */
    @TableField(value = "exchange")
    private String exchange;

    /**
     * 路由键
     */
    @TableField(value = "routing_key")
    private String routingKey;

    /**
     * 状态: 0投递中 1投递成功 2投递失败 3已消费
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 重试次数
     */
    @TableField(value = "try_count")
    private Integer tryCount;

    /**
     * 下一次重试时间
     */
    @TableField(value = "next_try_time")
    private Date nextTryTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}