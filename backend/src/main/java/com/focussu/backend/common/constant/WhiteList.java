package com.focussu.backend.common.constant;

import lombok.Getter;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Getter
public enum WhiteList {
    DOCS("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/docs"),
    CHECKER("/test/health-check"),
    AUTH("/api/members/join", "/auth/login/**"),
    AI_ANALYSIS("/ai-analysis", "/analysis-document");

    private final String[] patterns;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    WhiteList(String... patterns) {
        this.patterns = patterns;
    }

    /**
     * whitelist로 구성할 URL 패턴들을 enum에서 DOCS와 CHECKER 카테고리만 결합함.
     */
    public static boolean isWhitelisted(String uri) {
        return Arrays.stream(WhiteList.DOCS.getPatterns())
                .anyMatch(pattern -> pathMatcher.match(pattern, uri))
                || Arrays.stream(WhiteList.AUTH.getPatterns())
                .anyMatch(pattern -> pathMatcher.match(pattern, uri))
                || Arrays.stream(WhiteList.CHECKER.getPatterns())
                .anyMatch(pattern -> pathMatcher.match(pattern, uri))
                || Arrays.stream(WhiteList.AI_ANALYSIS.getPatterns())
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
}
