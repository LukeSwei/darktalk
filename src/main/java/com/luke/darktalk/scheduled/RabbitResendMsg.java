package com.luke.darktalk.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luke.darktalk.contant.RabbitConstant;
import com.luke.darktalk.model.domain.MsgLog;
import com.luke.darktalk.service.MsgLogService;
import com.luke.darktalk.utils.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Rabbitmq消息重发
 *
 * @author caolu
 * @date 2023/04/23
 */
@Component
@Slf4j
public class RabbitResendMsg {

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 最大投递次数
    private static final int MAX_TRY_COUNT = 3;

    /**
     * 每30s拉取投递失败的消息, 重新投递
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void resend() {
        log.info("开始执行定时任务(重新投递消息)");


//        List<MsgLog> msgLogList = msgLogService.selectTimeoutMsg();
        QueryWrapper<MsgLog> wrapper = new QueryWrapper<>();
        wrapper.eq("status",RabbitConstant.DELIVER_IN);
        wrapper.lt("next_try_time",new Date());
        List<MsgLog> msgLogList = msgLogService.list(wrapper);
        msgLogList.forEach(msgLog -> {
            String msgId = msgLog.getMsgId();
            if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                msgLogService.updateStatus(msgId, RabbitConstant.DELIVER_FAIL);
                log.info("超过最大重试次数, 消息投递失败, msgId: {}", msgId);
            } else {
                msgLogService.updateTryCount(msgId, msgLog.getNextTryTime());// 投递次数+1

                CorrelationData correlationData = new CorrelationData(msgId);
                rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), MessageHelper.objToMsg(msgLog.getMsg()), correlationData);// 重新投递

                log.info("第 " + (msgLog.getTryCount() + 1) + " 次重新投递消息");
            }
        });

        log.info("定时任务执行结束(重新投递消息)");
    }

}
