package com.example.finance.dashboard.service;

import com.example.finance.dashboard.dto.request.UserRequest;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));


        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Status.ACTIVE)
                .role(role)   // ✅ single role
                .build();

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}