package com.focussu.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.auth.filter.AuthExceptionFilter;
import com.focussu.backend.auth.filter.JwtAuthenticationFilter;
import com.focussu.backend.auth.filter.LoginFilter;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.constant.WhiteList;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthenticationConfiguration authConfig;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    private final AuthExceptionFilter authExceptionFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter filter = new LoginFilter(userDetailsService, jwtTokenUtil, tokenService);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public LogoutHandler jwtLogoutHandler() {
        return (request, response, authentication) -> {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                log.debug("[LOGOUT HANDLER] No valid Bearer token found in Authorization header.");
                return; // 유효한 헤더가 없으면 처리 중단
            }
            jwt = authHeader.substring(7);

            // 1. Redis에서 토큰으로 사용자 이름 조회 (토큰 존재 여부 확인)
            Optional<String> usernameOptional = tokenService.getUsernameByToken(jwt);

            if (usernameOptional.isPresent()) {
                // 2. 토큰이 Redis에 존재하는 경우 -> 삭제 처리
                String username = usernameOptional.get();
                log.info("[LOGOUT HANDLER] Valid token found for user: {}. Proceeding with logout.", username);
                // removeToken 대신 removeTokenByUsername을 사용
                tokenService.removeTokenByUsername(username);
                log.info("[LOGOUT HANDLER] Token successfully removed for user: {}", username);
            } else {
                // 3. 토큰이 Redis에 존재하지 않는 경우 (이미 로그아웃되었거나 유효하지 않은 토큰)
                log.warn("[LOGOUT HANDLER] Logout attempt with token not found in Redis (already logged out or invalid). Token prefix: {}", jwt.substring(0, Math.min(jwt.length(), 10)) + "...");
                // Redis에 없으므로 추가 삭제 작업은 불필요.
                // LogoutSuccessHandler가 SecurityContext를 정리하는 등의 후처리는 정상 진행됨.
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(WhiteList.DOCS.getPatterns()).permitAll()
                        .requestMatchers(WhiteList.AUTH.getPatterns()).permitAll()
                        .requestMatchers(WhiteList.CHECKER.getPatterns()).permitAll()
                        .requestMatchers("/ws/signaling").permitAll()
                        .requestMatchers("/ai-analysis").permitAll()
                        .requestMatchers("/analysis-document").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authExceptionFilter, UsernamePasswordAuthenticationFilter.class) // 예외 처리
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)           // 로그인 처리
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // --- .logout() DSL 사용 ---
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler())
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // 로그아웃 성공 시 SecurityContext 클리어
                            SecurityContextHolder.clearContext();

                            // 응답 작성
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.writeValue(response.getWriter(), Map.of("message", "로그아웃 성공"));
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
