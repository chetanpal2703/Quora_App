package com.example.quora_app.feature.authorization.repository;

import com.example.quora_app.feature.authorization.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}
