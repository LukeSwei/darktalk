package com.luke.darktalk.model.request;

import lombok.Data;

/**
 * 分页请求
 *
 * @author caolu
 * @date 2023/04/15
 */
@Data
public class PageRequest {

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 当前页数
     */
    private int pageNum = 1;

    private long total;
}
