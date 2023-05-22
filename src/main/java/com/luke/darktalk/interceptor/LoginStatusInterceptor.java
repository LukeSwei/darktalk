package com.luke.darktalk.interceptor;

import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.contant.RedisConstant;
import com.luke.darktalk.contant.UserConstant;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.service.UserService;
import com.luke.darktalk.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 判断登录状态，并跳转到登录页面。
 *
 * @author caolu
 * @date 2023/05/01
 */
@Slf4j
public class LoginStatusInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果是OPTIONS请求的话 进行直接放行
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())){
            log.info("OPTIONS请求放行");
            return true;
        }
        //获取Cookie请求中的Cookie值，如果没有则跳转到登录页面。
//        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        String header = request.getHeader(UserConstant.USER_LOGIN_HEADER_TOKEN);
        if (!JwtUtils.checkToken(header)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User userToken = JwtUtils.getUserByJwtToken(header);
        User currentUser = (User) redisTemplate.opsForHash().get(RedisConstant.LOGIN_TOKEN, userToken.getUserAccount());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        if (safetyUser!=null) {
            return true;
        }
        return false;
    }
}
