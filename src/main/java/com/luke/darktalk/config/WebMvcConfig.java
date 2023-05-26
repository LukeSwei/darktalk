package com.luke.darktalk.config;

import com.luke.darktalk.interceptor.LoginStatusInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc配置
 *
 * @author caolu
 * @date 2023/04/13
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public LoginStatusInterceptor loginStatusInterceptor(){return new LoginStatusInterceptor();}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginStatusInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/registry","/user/verify","/user/forgetPwd","/user/login","/user/changePwd",
                        "/test/**",
                        "/swagger-ui/**","/doc.html/**","/favicon.ico","/webjars/**","/swagger-resources/**","/v2/**")
                .excludePathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOriginPatterns("*")
//                .allowedOrigins("http://192.168.137.95:8080","http://darktalk.overend.top","http://localhost:8082","http://192.168.137.215:8080")
                //是否允许证书，默认不开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //设置允许的header属性
                .maxAge(3600);
    }

}
