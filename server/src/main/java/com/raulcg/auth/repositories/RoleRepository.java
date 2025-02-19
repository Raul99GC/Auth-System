package com.raulcg.auth.repositories;

import com.raulcg.auth.enums.UserRole;
import com.raulcg.auth.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(UserRole roleName);
}
