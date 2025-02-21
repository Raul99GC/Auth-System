package com.raulcg.auth.repositories;

import com.raulcg.auth.models.AccountValidationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountValidationTokenRepository extends JpaRepository<AccountValidationToken, UUID> {
}
