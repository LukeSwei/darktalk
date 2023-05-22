package com.luke.darktalk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.model.domain.Message;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.dto.MessageDto;
import com.luke.darktalk.service.MessageService;
import com.luke.darktalk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 消息接口
 *
 * @author caolu
 * @date 2023/04/20
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    /**
     * 列表后
     * 查询消息接口
     *
     * @return {@link BaseResponse}<{@link List}<{@link Message}>>
     */
    @GetMapping("/list/page")
    public BaseResponse<List<Message>> listMessage(@RequestBody MessageDto messageDto, HttpServletRequest request){
        if(messageDto==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        int pageSize = messageDto.getPageSize();
        int pageNum = messageDto.getPageNum();
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //查询消息列表
        Page<Message> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("from",loginUser.getId());
        wrapper.eq("to",messageDto.getChatId());
        wrapper.orderByAsc("createTime");
        Page<Message> messagePage = messageService.page(page, wrapper);
        return ResultUtils.success(messagePage.getRecords());
    }
}
