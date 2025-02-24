package com.raulcg.auth.services.refreshToken;

import com.raulcg.auth.models.RefreshToken;
import com.raulcg.auth.models.User;

public interface IRefreshTokenService {

    RefreshToken createToken(User user, int expiryDays);

    RefreshToken disabelToken(RefreshToken token);
}
