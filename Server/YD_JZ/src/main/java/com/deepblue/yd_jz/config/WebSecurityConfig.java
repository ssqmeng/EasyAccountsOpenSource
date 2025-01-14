package com.deepblue.yd_jz.config;

// WebSecurityConfig.java

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // 禁用 CSRF（Cross-Site Request Forgery）
                .authorizeRequests()
                .antMatchers("/auth/login").permitAll()  // 登录接口不需要认证
                .anyRequest().authenticated()          // 其他接口需要认证
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 在过滤器链前添加 JwtAuthenticationFilter
    }
}
