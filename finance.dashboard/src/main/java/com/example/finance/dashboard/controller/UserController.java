package com.example.finance.dashboard.controller;


import com.example.finance.dashboard.dto.request.UserRequest;
import com.example.finance.dashboard.dto.response.UserResponse;
import com.example.finance.dashboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //  Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //  Get Profile
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getMyProfile(auth.getName()));
    }

    //  Update Profile
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UserRequest request,
            Authentication auth) {

        return ResponseEntity.ok(
                userService.updateProfile(auth.getName(), request)
        );
    }

    //  Delete Account
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount(Authentication auth) {
        userService.deleteAccount(auth.getName());
        return ResponseEntity.ok("Account deleted successfully");
    }
}