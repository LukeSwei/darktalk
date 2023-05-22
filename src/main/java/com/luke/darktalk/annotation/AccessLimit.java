package com.luke.darktalk.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 访问限制注解
 *
 * @author caolu
 * @date 2023/04/21
 */
@Retention(RUNTIME)
@Target({METHOD,TYPE})
public @interface AccessLimit {

    /**
     * 秒
     *
     * @return long
     */
    long seconds() default 5L;

    /**
     * 最大访问限制
     *
     * @return long
     */
    long maxTime() default 3L;

    /**
     * 锁定时间
     *
     * @return long
     */
    long lockTime() default 120L;
}
