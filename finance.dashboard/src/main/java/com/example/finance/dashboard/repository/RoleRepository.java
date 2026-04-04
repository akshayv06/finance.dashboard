package com.example.finance.dashboard.repository;

import com.example.finance.dashboard.model.Role;
import com.example.finance.dashboard.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}