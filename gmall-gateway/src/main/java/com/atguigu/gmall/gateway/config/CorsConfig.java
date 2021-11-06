package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig {

    /**
     * 注册一个spring webflux响应式的跨域过滤器
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://manager.gmall.com");//允许哪些域名可以跨域访问 若设置为*则不可携带cookie
        configuration.setAllowCredentials(true);//允许携带cookie
        configuration.addAllowedHeader("*");//允许任何头信息
        configuration.addAllowedMethod("*");//允许任何请求方法
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(configurationSource);
    }
}
