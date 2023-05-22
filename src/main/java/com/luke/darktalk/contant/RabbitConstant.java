package com.luke.darktalk.contant;

/**
 * RabbitMQ常量
 *
 * @author caolu
 * @date 2023/04/23
 */
public interface RabbitConstant {

    /**
     * 投递中
     */
    int DELIVER_IN = 0;

    /**
     * 投递成功
     */
    int DELIVER_SUCCESS = 1;

    /**
     * 投递消息失败
     */
    int DELIVER_FAIL = 2;

    /**
     * 消息已被消费
     */
    int DELIVER_CONSUMED = 3;
}
