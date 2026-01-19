package com.example.doktoribackend.auth.controller;

import com.example.doktoribackend.auth.OAuthService;
import com.example.doktoribackend.auth.component.OAuthServiceFactory;
import com.example.doktoribackend.auth.dto.OAuthProvider;
import com.example.doktoribackend.common.util.CookieUtil;
import com.example.doktoribackend.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final OAuthServiceFactory oauthFactory;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/auth/{provider}")
    public RedirectView redirectToOAuth(
            @PathVariable String provider,
            @RequestParam(required = false) String state) {

        OAuthProvider oauthProvider = OAuthProvider.fromString(provider);

        OAuthService service = oauthFactory.getService(oauthProvider);
        return new RedirectView(service.buildAuthorizeUrl(state));
    }

    @GetMapping("/auth/{provider}/callback")
    public RedirectView handleCallback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response) {

        OAuthProvider oauthProvider = OAuthProvider.fromString(provider);
        OAuthService service = oauthFactory.getService(oauthProvider);

        CookieUtil.addRefreshTokenCookie(
                response,
                service.handleCallback(code),
                jwtTokenProvider.getRefreshExpSeconds()
        );

        String redirectUrl = service.buildFrontendRedirect(state);
        return new RedirectView(redirectUrl);
    }
}
