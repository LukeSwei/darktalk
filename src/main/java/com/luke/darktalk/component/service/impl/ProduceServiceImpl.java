package com.luke.darktalk.component.service.impl;

import cn.hutool.json.JSONUtil;
import com.luke.darktalk.component.mail.Mail;
import com.luke.darktalk.component.service.ProduceService;
import com.luke.darktalk.config.RabbitConfig;
import com.luke.darktalk.model.domain.MsgLog;
import com.luke.darktalk.service.MsgLogService;
import com.luke.darktalk.utils.MessageHelper;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 生产服务impl
 *
 * @author caolu
 * @date 2023/04/23
 */
@Service
public class ProduceServiceImpl implements ProduceService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MsgLogService msgLogService;

    @Override
    public boolean send(Mail mail) {
        //创建uuid
        String msgId = UUID.randomUUID().toString().replaceAll("-", "");
        mail.setMsgId(msgId);

        MsgLog msgLog = new MsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setMsg(JSONUtil.parseObj(mail).toString());
        msgLog.setExchange(RabbitConfig.MAIL_EXCHANGE_NAME);
        msgLog.setRoutingKey(RabbitConfig.MAIL_ROUTING_KEY_NAME);
        msgLog.setCreateTime(new Date());
        msgLogService.save(msgLog);

        //发送消息到rabbitMQ
        CorrelationData correlationData = new CorrelationData(msgId);

        rabbitTemplate.convertAndSend(RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, MessageHelper.objToMsg(mail),correlationData);

        return true;
    }

}
