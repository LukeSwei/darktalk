package com.luke.darktalk.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson配置
 *
 * @author caolu
 * @date 2023/04/13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    private int database;

    /**
     * redisson客户端
     *
     * @return {@link RedissonClient}
     */
    @Bean
    public RedissonClient redissonClient(){
        // 1. Create config object
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(database).setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
