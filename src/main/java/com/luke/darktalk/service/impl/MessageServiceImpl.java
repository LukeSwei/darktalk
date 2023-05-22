package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.model.domain.Message;
import com.luke.darktalk.service.MessageService;
import com.luke.darktalk.mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
* @author caolu
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2023-04-20 12:14:53
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




