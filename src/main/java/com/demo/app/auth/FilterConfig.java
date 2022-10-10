package com.demo.app.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        final FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(secret));
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.addUrlPatterns("/user/me");
        registrationBean.addUrlPatterns("/file/upload");
        registrationBean.addUrlPatterns("/file/delete/*");
        registrationBean.addUrlPatterns("/file/list");
        return registrationBean;
    }
}
