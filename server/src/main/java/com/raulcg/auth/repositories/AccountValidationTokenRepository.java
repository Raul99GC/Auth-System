package com.raulcg.auth.repositories;

import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountValidationTokenRepository extends JpaRepository<AccountValidationToken, UUID> {
    List<AccountValidationToken> findByUser(User user);

    AccountValidationToken findFirstByUserOrderByCreatedAtDesc(User user);
}
