package com.example.quora_app.feature.authorization.repository;

import com.example.quora_app.feature.authorization.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
