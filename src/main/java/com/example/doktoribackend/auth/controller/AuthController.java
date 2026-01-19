package com.example.doktoribackend.auth.controller;

import com.example.doktoribackend.auth.service.TokenService;
import com.example.doktoribackend.common.util.CookieUtil;
import com.example.doktoribackend.common.error.ErrorCode;
import com.example.doktoribackend.exception.BusinessException;
import com.example.doktoribackend.security.TokenResponse;
import com.example.doktoribackend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Operation(
            summary = "Access Token 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다. "
    )
    @PostMapping("/auth/refresh")
    public TokenResponse refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = CookieUtil.resolveRefreshToken(request);

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.NOT_EXIST_REFRESH_TOKEN);
        }

        TokenResponse refreshed = tokenService.refreshTokens(refreshToken);

        CookieUtil.addRefreshTokenCookie(
                response,
                refreshed.getRefreshToken(),
                jwtTokenProvider.getRefreshExpSeconds()
        );
        return refreshed;
    }

    @Operation(
            summary = "로그 아웃",
            description = "Refresh Token을 비활성화하고 쿠키를 삭제합니다 "
    )
    @PostMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.resolveRefreshToken(request);
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.logout(refreshToken);
        }
        CookieUtil.removeRefreshTokenCookie(response);
    }
}
