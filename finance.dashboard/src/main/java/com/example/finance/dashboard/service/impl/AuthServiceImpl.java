package com.example.finance.dashboard.service.impl;


import com.example.finance.dashboard.dto.response.AuthResponse;
import com.example.finance.dashboard.dto.request.LoginRequest;
import com.example.finance.dashboard.dto.request.UserRequest;
import com.example.finance.dashboard.exception.BadRequestException;
import com.example.finance.dashboard.exception.ResourceNotFoundException;
import com.example.finance.dashboard.exception.UnauthorizedException;
import com.example.finance.dashboard.model.Role;
import com.example.finance.dashboard.model.RoleName;
import com.example.finance.dashboard.model.Status;
import com.example.finance.dashboard.model.User;
import com.example.finance.dashboard.repository.RoleRepository;
import com.example.finance.dashboard.repository.UserRepository;
import com.example.finance.dashboard.service.AuthService;
import com.example.finance.dashboard.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if (request.getRole() == RoleName.ROLE_ADMIN) {
            throw new BadRequestException("Cannot assign ADMIN role");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Status.ACTIVE)
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return buildAuthResponse(user, token, "User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if (user.getStatus() != Status.ACTIVE) {
            throw new UnauthorizedException("User is inactive");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return buildAuthResponse(user, token, "Login successful");
    }

    // 🔥 COMMON METHOD (CLEAN CODE)
    private AuthResponse buildAuthResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .message(message)
                .build();
    }
}