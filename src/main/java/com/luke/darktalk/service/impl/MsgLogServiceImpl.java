package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.model.domain.MsgLog;
import com.luke.darktalk.service.MsgLogService;
import com.luke.darktalk.mapper.MsgLogMapper;
import com.luke.darktalk.utils.JodaTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【msg_log(消息投递日志)】的数据库操作Service实现
* @createDate 2023-04-23 16:18:22
*/
@Service
public class MsgLogServiceImpl extends ServiceImpl<MsgLogMapper, MsgLog> implements MsgLogService{

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Override
    public void updateStatus(String msgId, int deliverStatus) {
        MsgLog msgLog = new MsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setStatus(deliverStatus);
        msgLog.setUpdateTime(new Date());
        this.updateById(msgLog);
    }

    @Override
    public List<MsgLog> selectTimeoutMsg() {
        return msgLogMapper.selectTimeoutMsg();
    }

    @Override
    public void updateTryCount(String msgId, Date tryTime) {
        Date nextTryTime= JodaTimeUtil.plusMinutes(tryTime, 1);
        MsgLog msgLog = new MsgLog();
        msgLog.setMsgId(msgId);
        msgLog.setNextTryTime(nextTryTime);
        msgLogMapper.updateTryCount(msgLog);
    }


}




