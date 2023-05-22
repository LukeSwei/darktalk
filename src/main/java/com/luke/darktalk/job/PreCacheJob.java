package com.luke.darktalk.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.contant.RedisConstant;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 预热缓存
 *
 * @author caolu
 * @date 2023/04/13
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainList = new ArrayList<>();


    /**
     * 预热缓存推荐用户
     * TODO 主要用户
     */
    @Scheduled(cron = "0 58 23 * * *")
    public void doCacheRecommendUser() {
        mainList = Arrays.asList(1L);
        RLock rLock = redissonClient.getLock(RedisConstant.REDISSON_LOCK);
        try {
            if (rLock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainList) {
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), wrapper);
                    String redisKey = String.format(RedisConstant.REDIS_USER_RECOMMEND + userId);
                    try {
                        redisTemplate.opsForValue().set(redisKey, userPage, 1800000, TimeUnit.MICROSECONDS);
                    } catch (Exception e) {
                        log.error("redis set error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("redis分布式锁出现问题->PreCacheJOb", e);
            throw new RuntimeException(e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

    }
}
