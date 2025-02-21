package com.raulcg.auth.services.accountValidationToken;

import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;
import com.raulcg.auth.requires.ActivateAccountRequest;

public interface IAccountValidationTokenService {
    AccountValidationToken createToken(User user, String token);

    boolean validateToken(ActivateAccountRequest request);
}
