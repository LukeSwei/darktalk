package com.luke.darktalk.interceptor;

import com.luke.darktalk.annotation.AccessLimit;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 接口防刷拦截器
 *
 * @author caolu
 * @date 2023/04/21
 */
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static final String LOCK_PREFIX = "interface:lock:";

    private static final String COUNT_PREFIX = "interface:count:";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod targetMethod = (HandlerMethod) handler;
            //取出目标类中的注解
            AccessLimit targetClassAnnotation = targetMethod.getMethod().getDeclaringClass().getAnnotation(AccessLimit.class);
            boolean isBrushForAllInterface = false;
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            long seconds = 0L;
            long maxTime = 0L;
            long lockTime = 0L;
            if (!Objects.isNull(targetClassAnnotation)) {
                log.info("目标接口方法所在类上有@AccessLimit注解");
                isBrushForAllInterface = true;
                seconds = targetClassAnnotation.seconds();
                maxTime = targetClassAnnotation.maxTime();
                lockTime = targetClassAnnotation.lockTime();
            }
            // 取出目标方法中的 AccessLimit 注解
            AccessLimit accessLimit = targetMethod.getMethodAnnotation(AccessLimit.class);
            if (!Objects.isNull(accessLimit)) {
                // 需要进行防刷处理，接下来是处理逻辑
                seconds = accessLimit.seconds();
                maxTime = accessLimit.maxTime();
                lockTime = accessLimit.lockTime();
                if (isLock(seconds, maxTime, lockTime, ip, uri)) {
                    throw new BusinessException(ErrorCode.ACCESS_FREQUENT);
                }
            } else {
                // 目标接口方法处无@AccessLimit注解，但还要看看其类上是否加了(类上有加，代表针对此类下所有接口方法都要进行防刷处理)
                if (isBrushForAllInterface && isLock(seconds, maxTime, lockTime, ip, uri)) {
                    throw new BusinessException(ErrorCode.ACCESS_FREQUENT);
                }
            }
        }
        return true;
    }

    /**
     * 判断接口是否已经被禁用
     *
     * @param second   在指定时间内
     * @param maxTime  在指定时间内允许访问的最大次数
     * @param lockTime 接口被禁用的次数
     * @param ip       知识产权
     * @param uri      uri
     * @author caolu
     * @date 2023-4-21
     * @return boolean
     */
    private boolean isLock(long second, long maxTime, long lockTime, String ip, String uri) {
        String lockKey = LOCK_PREFIX + ip + uri;
        Object lock = redisTemplate.opsForValue().get(lockKey);
        if (Objects.isNull(lock)) {
            //说明未被禁用
            String countKey = COUNT_PREFIX + ip + uri;
            Object count = redisTemplate.opsForValue().get(countKey);
            if (Objects.isNull(countKey)) {
                log.info("首次访问，IP{}", ip);
                redisTemplate.opsForValue().set(countKey, 1, second, TimeUnit.SECONDS);
            } else {
                if ((Integer) count < maxTime) {
                    redisTemplate.opsForValue().increment(countKey);
                }else {
                    log.info("{}禁用访问{}",ip,uri);
                    redisTemplate.opsForValue().set(lockKey, 1, lockTime, TimeUnit.SECONDS);
                    redisTemplate.opsForValue().decrement(countKey);
                    return true;
                }
            }
        }else {
            return true;
        }
        return false;
    }
}
