package com.luke.darktalk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.model.domain.Follow;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.service.FollowService;
import com.luke.darktalk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.luke.darktalk.contant.UserConstant.USER_LOGIN_STATE;

@Slf4j
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private FollowService followService;


    @PostMapping("/addFollow")
    public BaseResponse  addFollow(HttpServletRequest request){
        String id = request.getParameter("id");
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        Follow follow = new Follow();
        follow.setFollowId(Long.valueOf(id));
        follow.setFollowId(loginUser.getId());
        boolean save = followService.save(follow);
        if(!save){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"关注失败");
        }
        return ResultUtils.success(null,"关注成功");
    }

    @PostMapping("/cancelFollow")
    public BaseResponse cancelFollow(HttpServletRequest request){
        String id = request.getParameter("id");
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",id);
        wrapper.eq("followId",loginUser.getId());
        boolean remove = followService.remove(wrapper);
        if(!remove){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"取关失败");
        }
        return ResultUtils.success(null,"取关成功");
    }

}
