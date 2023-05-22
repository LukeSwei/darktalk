package com.luke.darktalk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.model.domain.BuddyList;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.dto.BuddyDto;
import com.luke.darktalk.service.BuddyListService;
import com.luke.darktalk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 好友接口
 *
 * @author caolu
 * @date 2023/04/21
 */
@Slf4j
@RestController
@RequestMapping("/buddy")
public class BuddyController {

    @Resource
    private BuddyListService buddyListService;

    @Resource
    private UserService userService;


    @GetMapping("/list/page")
    public BaseResponse<List<BuddyList>> listBuddy(@RequestBody BuddyDto buddyDto, HttpServletRequest request){
        if(buddyDto==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        int pageSize = buddyDto.getPageSize();
        int pageNum = buddyDto.getPageNum();
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //查询消息列表
        Page<BuddyList> page = new Page<>(pageNum,pageSize);
        QueryWrapper<BuddyList> wrapper = new QueryWrapper<>();
        wrapper.eq("mineId",loginUser.getId());
        wrapper.like(StringUtils.isNotBlank(buddyDto.getSearchText()),"friendName",buddyDto.getSearchText());
        Page<BuddyList> buddyListPage = buddyListService.page(page, wrapper);
        //计算总数
        return ResultUtils.success(buddyListPage.getRecords());
    }

    @PostMapping("/add")
    public BaseResponse<Boolean> addBuddy(@RequestBody BuddyDto buddyDto, HttpServletRequest request){
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if(buddyDto==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        BuddyList buddyList = new BuddyList();
        BeanUtils.copyProperties(buddyDto, buddyList);
        boolean save = buddyListService.save(buddyList);
        if(!save){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(save);
    }

    @PostMapping("/del")
    public BaseResponse<Boolean> delBuddy(@RequestBody BuddyDto buddyDto, HttpServletRequest request){
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if(buddyDto==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<BuddyList> wrapper = new QueryWrapper<>();
        wrapper.eq("mineId",buddyDto.getMineId());
        wrapper.eq("friendId",buddyDto.getFriendId());
        boolean delete =  buddyListService.remove(wrapper);
        if(!delete) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(delete);
    }
}
