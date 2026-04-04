package com.example.finance.dashboard.dto.request;

import com.example.finance.dashboard.model.RoleName;
import lombok.Data;

@Data
public class UserRequest {

    private String name;
    private String email;
    private String password;
    private RoleName role;
}