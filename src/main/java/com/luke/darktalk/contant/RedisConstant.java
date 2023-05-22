package com.luke.darktalk.contant;

/**
 * Redis常量
 *
 * @author caolu
 * @date 2023/04/13
 */
public interface RedisConstant {

    String LOGIN_TOKEN = "user:token:";

    String REDISSON_LOCK = "darktalk:precacheJob:doCacheRecommendUser:lock";

    String REDIS_USER_RECOMMEND = "darktalk:user:recommend:";

    String LOGIN_VERIFY_CODE = "login:verify:code:";
}
