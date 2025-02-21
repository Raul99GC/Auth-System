package com.raulcg.auth.services.accountValidationToken;

import com.raulcg.auth.models.AccountValidationToken;
import com.raulcg.auth.models.User;

public interface IAccountValidationTokenService {
    AccountValidationToken createToken(User user, String token);
}
