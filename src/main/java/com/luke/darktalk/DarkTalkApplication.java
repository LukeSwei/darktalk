package com.luke.darktalk;

import cn.xuyanwu.spring.file.storage.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


//@MapperScan("com.luke.darktalk.mapper")
@EnableFileStorage
@EnableScheduling
@SpringBootApplication
public class DarkTalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(DarkTalkApplication.class, args);
    }

}
