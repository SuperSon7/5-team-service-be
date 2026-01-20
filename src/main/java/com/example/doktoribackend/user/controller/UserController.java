package com.example.doktoribackend.user.controller;

import com.example.doktoribackend.common.response.ApiResult;
import com.example.doktoribackend.security.CustomUserDetails;
import com.example.doktoribackend.user.dto.OnboardingRequest;
import com.example.doktoribackend.user.dto.UserProfileResponse;
import com.example.doktoribackend.user.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final OnboardingService onboardingService;

    @Operation(summary = "온보딩", description = "소셜 로그인 이후 사용자의 온보딩 정보를 저장합니다.")
    @PutMapping("/me/onboarding")
    public ResponseEntity<ApiResult<UserProfileResponse>> onboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OnboardingRequest request
    ) {
        UserProfileResponse response = onboardingService.onboard(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResult.ok(response));
    }
}
