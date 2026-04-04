package com.example.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String type; // Bearer

    private Long userId;
    private String name;
    private String email;
    private String role;

    private String message;
}