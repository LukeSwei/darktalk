package com.luke.darktalk.config;

import com.luke.darktalk.utils.MinIoUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIo配置类
 *
 * @author luke
 * @date 2023/05/19
 */
@Configuration
@Data
public class MinIoConfig {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.fileHost}")
    private String fileHost;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.imgSize}")
    private Integer imgSize;
    @Value("${minio.fileSize}")
    private Integer fileSize;

    @Bean
    public MinIoUtils creatMinioClient() {
        return new MinIoUtils(endpoint, bucketName, accessKey, secretKey, imgSize, fileSize);
    }
}
