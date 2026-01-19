package com.example.doktoribackend.auth;

import com.example.doktoribackend.auth.dto.OAuthProvider;

public interface OAuthService {

    String buildAuthorizeUrl(String state);

    String handleCallback(String code);

    String buildFrontendRedirect(String state);

    OAuthProvider getProvider();
}
