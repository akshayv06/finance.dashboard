package com.example.finance.dashboard.service;

import com.example.finance.dashboard.dto.response.AuthResponse;
import com.example.finance.dashboard.dto.request.LoginRequest;
import com.example.finance.dashboard.dto.request.UserRequest;


public interface AuthService {

    AuthResponse register(UserRequest request);

    AuthResponse login(LoginRequest request);

}