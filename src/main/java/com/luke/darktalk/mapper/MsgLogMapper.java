package com.luke.darktalk.mapper;

import com.luke.darktalk.model.domain.MsgLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【msg_log(消息投递日志)】的数据库操作Mapper
* @createDate 2023-04-23 16:18:22
* @Entity com.luke.darktalk.model.domain.MsgLog
*/
public interface MsgLogMapper extends BaseMapper<MsgLog> {

    void updateStatusById(@Param("msgId") String msgId, @Param("status") int deliverStatus);

    List<MsgLog> selectTimeoutMsg();

    void updateTryCount(MsgLog msgLog);
}




