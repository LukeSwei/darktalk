package com.luke.darktalk.component.mail.rabbitmq;

import com.luke.darktalk.component.mail.Mail;
import com.luke.darktalk.config.RabbitConfig;
import com.luke.darktalk.contant.RabbitConstant;
import com.luke.darktalk.model.domain.MsgLog;
import com.luke.darktalk.service.MsgLogService;
import com.luke.darktalk.utils.JsonUtil;
import com.luke.darktalk.utils.SendMailUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 邮件消费者服务
 *
 * @author caolu
 * @date 2023/04/23
 */
@Component
@Slf4j
public class MailConsumerService {

    @Autowired
    private SendMailUtil sendMailUtil;

    @Autowired
    private MsgLogService msgLogService;

    @RabbitListener(queues = RabbitConfig.MAIL_QUEUE_NAME)
    public void consume(Message message, Channel channel) throws IOException {
        //将消息转化为对象
        String str = new String(message.getBody());
        Mail mail = JsonUtil.strToObj(str, Mail.class);
        log.info("收到消息: {}", mail.toString());

        String msgId = mail.getMsgId();

        MsgLog msgLog = msgLogService.getById(msgId);
        // 消费幂等性
        if (null == msgLog || msgLog.getStatus() == RabbitConstant.DELIVER_CONSUMED) {
            log.info("重复消费, msgId: {}", msgId);
            return;
        }
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();

        boolean success = sendMailUtil.send(mail);
        if (success) {
            msgLogService.updateStatus(msgId,RabbitConstant.DELIVER_CONSUMED);
            // 消费确认
            channel.basicAck(tag, false);
        } else {
            channel.basicNack(tag, false, true);
        }
    }
}