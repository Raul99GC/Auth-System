package com.raulcg.auth.repositories;

import com.raulcg.auth.models.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP, UUID> {
}
