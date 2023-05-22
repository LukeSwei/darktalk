package com.luke.darktalk.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.contant.CommonConstant;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.model.domain.Tag;
import com.luke.darktalk.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 标签控制器
 *
 * @author caolu
 * @date 2023/04/24
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    @GetMapping("/all")
    public BaseResponse<List<Tag>> listAll() {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("isDelete", CommonConstant.DELETE_FALSE);
        List<Tag> tagList = tagService.list(wrapper);
        return ResultUtils.success(tagList);
    }

}
