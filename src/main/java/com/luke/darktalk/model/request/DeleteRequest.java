package com.luke.darktalk.model.request;


import lombok.Data;

/**
 * 删除请求
 *
 * @author caolu
 * @date 2023/04/17
 */
@Data
public class DeleteRequest {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long id;
}
