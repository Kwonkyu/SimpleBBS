package com.haruhiism.bbs.config;

import com.haruhiism.bbs.interceptor.LoginInterceptor;
import com.haruhiism.bbs.interceptor.LogoutInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor redirectIfLogined;
    private final LogoutInterceptor redirectIfNotLogined;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(redirectIfLogined)
                .addPathPatterns(
                        "/account/login",
                        "/account/register");

        registry.addInterceptor(redirectIfNotLogined)
                .addPathPatterns(
                        "/account/logout",
                        "/account/manage/**",
                        "/account/withdraw");
    }
}
