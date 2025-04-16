package com.focussu.backend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.common.dto.ErrorResponse;
import com.focussu.backend.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(0) // 가장 먼저 실행되도록 설정 (낮은 숫자가 우선순위)
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 이후 필터 (예: JwtRequestFilter) 실행
            filterChain.doFilter(request, response);
        } catch (AuthException ex) {
            // AuthException 발생 시, 해당 에러 응답 처리 (예: 토큰 만료, 형식 오류 등)
            response.setStatus(ex.getErrorCode().getStatus());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(ex.getErrorCode()));
        } catch (Exception ex) {
            // 그 외 발생한 예외는 내부 서버 에러로 처리
            response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
