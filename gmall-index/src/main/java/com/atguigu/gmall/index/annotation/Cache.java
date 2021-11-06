package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    String prefix() default "";

    /**
     * 缓存的有效时间 单位默认是分钟
     * @return
     */
    int timeout() default 10;

    String lock() default "";

    /**
     * 防止缓存雪崩 设置的随机时间 单位默认是分钟
     * @return
     */
    int random() default 1;
}
