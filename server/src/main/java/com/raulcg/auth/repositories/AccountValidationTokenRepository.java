package com.raulcg.auth.repositories;

import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountValidationTokenRepository extends JpaRepository<AccountValidationToken, UUID> {
    AccountValidationToken findByUser(User user);
}
