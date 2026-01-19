package com.example.doktoribackend.auth;

import com.example.doktoribackend.auth.dto.OAuthProvider;
import com.example.doktoribackend.security.TokenResponse;

public interface OAuthService {

    String buildAuthorizeUrl(String state);

    TokenResponse handleCallback(String code);

    String buildFrontendRedirect(String state);

    OAuthProvider getProvider();
}
