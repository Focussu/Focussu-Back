package com.focussu.backend.auth.dto;

public record AuthenticationRequest(
        String email,
        String password
) { }
