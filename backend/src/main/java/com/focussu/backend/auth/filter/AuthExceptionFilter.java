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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(req, res);
        } catch (AuthException ex) {
            writeError(res, ex.getErrorCode());
        } catch (Exception ex) {
            writeError(res, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeError(HttpServletResponse res, ErrorCode code) throws IOException {
        res.setCharacterEncoding("UTF-8");
        res.setStatus(code.getStatus());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getWriter(), ErrorResponse.from(code));
    }
}
