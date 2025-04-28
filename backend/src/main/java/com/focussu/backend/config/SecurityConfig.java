package com.focussu.backend.config;

import com.focussu.backend.auth.filter.AuthExceptionFilter;
import com.focussu.backend.auth.filter.LoginFilter;
import com.focussu.backend.auth.filter.LogoutFilter;
import com.focussu.backend.auth.filter.JwtAuthenticationFilter;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.constant.WhiteList;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authConfig;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    private final AuthExceptionFilter     authExceptionFilter;
    private final LogoutFilter            logoutFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1) AuthenticationManager 빈
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 2) LoginFilter 빈: 생성자 주입 + setAuthenticationManager 필수!
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter(userDetailsService, jwtTokenUtil, tokenService);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    // 3) SecurityFilterChain 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuration
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Filter logics
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(WhiteList.DOCS.getPatterns()).permitAll()
                        .requestMatchers(WhiteList.AUTH.getPatterns()).permitAll()
                        .requestMatchers(WhiteList.CHECKER.getPatterns()).permitAll()
                        .anyRequest().authenticated()
                )
                // 1) 예외 처리 필터   : 로그인 직전
                .addFilterBefore(authExceptionFilter, UsernamePasswordAuthenticationFilter.class)
                // 2) 로그인 필터      : 스프링 기본 로그인 필터 위치
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                // 3) 로그아웃 필터    : 로그인 필터 바로 다음
                .addFilterAfter(logoutFilter, UsernamePasswordAuthenticationFilter.class)
                // 4) JWT 인증 필터   : BasicAuth 필터 바로 이전
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    // 4) PasswordEncoder 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
