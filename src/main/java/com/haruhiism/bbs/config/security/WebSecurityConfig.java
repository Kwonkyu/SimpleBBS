package com.haruhiism.bbs.config.security;

import com.haruhiism.bbs.domain.ManagerLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService accountService;


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(accountService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                    .antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // permit public services
                    .antMatchers("/", "/account/register**", "/account/login**", "/account/recovery/**", "/board/**", "/comment/**")
                            .permitAll().and()
                .authorizeRequests()
                    .antMatchers("/manage/**").hasAnyAuthority(
                            ManagerLevel.ACCOUNT_MANAGER.toString(),
                            ManagerLevel.BOARD_MANAGER.toString())
                    .antMatchers("/manage/console/article", "/manage/console/comment").hasAuthority(
                            ManagerLevel.BOARD_MANAGER.toString())
                    .antMatchers("/manage/console/account/**").hasAuthority(
                            ManagerLevel.ACCOUNT_MANAGER.toString())
                    .anyRequest()
                            .authenticated().and()
                .formLogin() // configure login form
                    .loginPage("/account/login")
                    .loginProcessingUrl("/account/login")
                            .permitAll()
                    .usernameParameter("userId") // in our form, 'username' field is 'userid'.
                    .defaultSuccessUrl("/account/manage")
//                    .failureUrl("/account/login?failed") // if failed, try again.
                    .failureHandler((request, response, exception) -> {
                        if(exception instanceof LockedException) response.sendRedirect("/account/login?locked");
                        else response.sendRedirect("/account/login?failed");
                    })
                    .and()
                .logout() // configure logout form
                    .logoutUrl("/account/logout") // need to be POST!
                    .permitAll()
                    .logoutSuccessUrl("/board/list")
                    .and()
                .httpBasic();
    }
}
