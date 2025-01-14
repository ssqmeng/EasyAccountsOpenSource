package com.deepblue.yd_jz.utils;

import com.deepblue.yd_jz.config.HttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HttpRequestInterceptor httpRequestInterceptor;

    // 配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //设置跨域得拦截器 ,/** 表示拦截所有请求
        registry.addInterceptor(httpRequestInterceptor).addPathPatterns("/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();

        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 可访问的外部域
        config.addAllowedOrigin("*");
        // 需要跨域用户凭证（cookie、HTTP认证及客户端SSL证明等）
        //config.setAllowCredentials(true);
        //config.addAllowedOriginPattern("*");

        // 跨域路径配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
