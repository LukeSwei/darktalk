package com.luke.darktalk.model.dto;

import com.luke.darktalk.model.request.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息传输实体类
 * @TableName message
 */
@Data
public class MessageDto extends PageRequest implements Serializable {

    private Integer chatId;

    private static final long serialVersionUID = 1L;
}