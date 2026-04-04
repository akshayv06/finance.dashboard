package com.example.finance.dashboard.service;

import com.example.finance.dashboard.dto.request.UserRequest;
import com.example.finance.dashboard.dto.response.UserResponse;
import com.example.finance.dashboard.exception.BadRequestException;
import com.example.finance.dashboard.exception.ResourceNotFoundException;
import com.example.finance.dashboard.model.Role;
import com.example.finance.dashboard.model.Status;
import com.example.finance.dashboard.model.User;
import com.example.finance.dashboard.repository.RoleRepository;
import com.example.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


    @Service
    @RequiredArgsConstructor
    public class UserService {
        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        // 👤 GET PROFILE
        public UserResponse getMyProfile(String email) {
            User user = getUserByEmail(email);
            return mapToResponse(user);
        }

        // ✏️ UPDATE PROFILE
        public UserResponse updateProfile(String email, UserRequest request) {
            User user = getUserByEmail(email);

            if (request.getName() != null) {
                user.setName(request.getName());
            }

            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            return mapToResponse(userRepository.save(user));
        }

        // ❌ DELETE ACCOUNT
        public void deleteAccount(String email) {
            User user = getUserByEmail(email);

            // Soft delete (BEST PRACTICE)
            user.setStatus(Status.INACTIVE);

            userRepository.save(user);
        }

        // 🔍 HELPER
        private User getUserByEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found"));
        }

        // 🔁 MAPPER
        private UserResponse mapToResponse(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().getName().name())
                    .status(user.getStatus().name())
                    .build();
        }

        // 👑 ADMIN API
        public List<UserResponse> getAllUsers() {
            return userRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }
    }