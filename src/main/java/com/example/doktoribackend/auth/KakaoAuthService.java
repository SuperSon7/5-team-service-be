package com.example.doktoribackend.auth;

import com.example.doktoribackend.auth.dto.KakaoTokenResponse;
import com.example.doktoribackend.auth.dto.KakaoUserResponse;
import com.example.doktoribackend.auth.dto.OAuthProvider;
import com.example.doktoribackend.common.error.ErrorCode;
import com.example.doktoribackend.exception.BusinessException;
import com.example.doktoribackend.security.jwt.JwtTokenProvider;
import com.example.doktoribackend.user.domain.*;
import com.example.doktoribackend.user.repository.UserAccountRepository;
import com.example.doktoribackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements OAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.oauth.frontend-redirect}")
    private String frontendRedirect;

    @Override
    public String buildAuthorizeUrl(String state) {
        return kakaoOAuthClient.buildAuthorizeUrl(state);
    }

    @Override
    @Transactional
    public String handleCallback(String code) {
        KakaoTokenResponse tokenResponse = kakaoOAuthClient.exchangeToken(code);

        KakaoUserResponse userResponse = kakaoOAuthClient.fetchUser(tokenResponse.getAccessToken());
        Long kakaoId = userResponse.getId();

        Optional<UserAccount> userAccountOpt = userAccountRepository
                .findByProviderAndProviderId(OAuthProvider.KAKAO, String.valueOf(kakaoId));

        if (userAccountOpt.isPresent()) {
            return handleExistingUser(userAccountOpt.get());
        }

        return handleNewUser(userResponse);
    }

    @Override
    public String buildFrontendRedirect(String state) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(frontendRedirect);
        if (state != null && !state.isBlank()) {
            builder.queryParam("state", state);
        }
        return builder.build().toUriString();
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }


    private String handleExistingUser(UserAccount userAccount) {
        User user = userAccount.getUser();

        if (user.isDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return jwtTokenProvider.createRefreshToken(user);
    }

    private String handleNewUser(KakaoUserResponse userResponse) {
        Long kakaoId = userResponse.getId();

        String nickname = extractNickname(userResponse, kakaoId);
        String profileImagePath = extractProfileImage(userResponse);
        Gender gender = extractGender(userResponse);
        Integer birthYear = extractBirthYear(userResponse);

        User newUser = createUser(nickname, profileImagePath, String.valueOf(kakaoId), gender, birthYear);

        return jwtTokenProvider.createRefreshToken(newUser);
    }

    private User createUser(String nickname, String profileImagePath, String providerId,
                            Gender gender, Integer birthYear) {
        User user = User.builder()
                .nickname(nickname)
                .profileImagePath(profileImagePath)
                .isOnboardingCompleted(false)
                .build();

        UserAccount userAccount = UserAccount.builder()
                .user(user)
                .provider(OAuthProvider.KAKAO)
                .providerId(providerId)
                .build();

        UserPreference userPreference = UserPreference.builder()
                .user(user)
                .gender(gender)
                .birthYear(birthYear)
                .build();

        UserStat userStat = UserStat.builder()
                .user(user)
                .build();

        user.linkAccount(userAccount);
        user.linkPreference(userPreference);
        user.linkStat(userStat);

        return userRepository.save(user);
    }

    private String extractNickname(KakaoUserResponse userResponse, Long kakaoId) {
        String rawNickname = null;

        if (userResponse.getKakaoAccount() != null
                && userResponse.getKakaoAccount().getProfile() != null) {
            rawNickname = userResponse.getKakaoAccount().getProfile().getNickname();
        }

        if (rawNickname == null || rawNickname.isBlank()) {
            return "kakao-" + kakaoId;
        }

        return rawNickname;
    }

    private String extractProfileImage(KakaoUserResponse userResponse) {
        if (userResponse.getKakaoAccount() != null
                && userResponse.getKakaoAccount().getProfile() != null) {
            return userResponse.getKakaoAccount().getProfile().getProfileImageUrl();
        }
        return null;
    }

    private Gender extractGender(KakaoUserResponse userResponse) {
        if (userResponse.getKakaoAccount() == null
                || userResponse.getKakaoAccount().getGender() == null) {
            return Gender.UNKNOWN;
        }

        String kakaoGender = userResponse.getKakaoAccount().getGender();
        return Gender.fromKakaoValue(kakaoGender);
    }

    private Integer extractBirthYear(KakaoUserResponse userResponse) {
        if (userResponse.getKakaoAccount() == null
                || userResponse.getKakaoAccount().getBirthyear() == null) {
            return null;
        }

        String birthYear = userResponse.getKakaoAccount().getBirthyear();

        try {
            return Integer.parseInt(birthYear.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}