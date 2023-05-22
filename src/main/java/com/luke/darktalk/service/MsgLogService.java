package com.luke.darktalk.service;

import com.luke.darktalk.model.domain.MsgLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【msg_log(消息投递日志)】的数据库操作Service
* @createDate 2023-04-23 16:18:22
*/
public interface MsgLogService extends IService<MsgLog> {

    void updateStatus(String msgId, int deliverConsumed);

    List<MsgLog> selectTimeoutMsg();

    void updateTryCount(String msgId, Date nextTryTime);
}
