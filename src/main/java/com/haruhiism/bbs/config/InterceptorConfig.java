package com.haruhiism.bbs.config;

import com.haruhiism.bbs.interceptor.*;
import com.haruhiism.bbs.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final LoginInterceptor redirectIfLogined;
    private final LogoutInterceptor redirectIfNotLogined;
    private final ManagerInterceptor managerInterceptor;
    private final BoardManagerInterceptor boardManagerInterceptor;
    private final AccountManagerInterceptor accountManagerInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // https://javapapers.com/spring/spring-mvc-handler-interceptor/
        // The order of registered interceptors is preserved.
        registry.addInterceptor(redirectIfLogined)
                .addPathPatterns(
                        "/account/login",
                        "/account/register");

        registry.addInterceptor(redirectIfNotLogined)
                .addPathPatterns(
                        "/account/logout",
                        "/account/manage/**",
                        "/account/withdraw",
                        "/manage/**");

        // TODO: manager interceptor.
        registry.addInterceptor(managerInterceptor)
                .addPathPatterns(
                        "/manage/**");

        registry.addInterceptor(boardManagerInterceptor)
                .addPathPatterns(
                        "/manage/article",
                        "/manage/comment");

        registry.addInterceptor(accountManagerInterceptor)
                .addPathPatterns(
                        "/manage/account");
    }
}
