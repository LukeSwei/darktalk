package com.luke.darktalk.model.dto;

import lombok.Data;

/**
 * 标签传输层协议
 *
 * @author caolu
 * @date 2023/04/24
 */
@Data
public class TagDto {

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 父id
     */
    private int parentId;



}
