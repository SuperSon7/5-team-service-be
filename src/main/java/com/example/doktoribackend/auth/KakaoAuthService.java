package com.example.doktoribackend.auth;

import com.example.doktoribackend.auth.dto.KakaoTokenResponse;
import com.example.doktoribackend.auth.dto.KakaoUserResponse;
import com.example.doktoribackend.auth.dto.OAuthProvider;
import com.example.doktoribackend.security.jwt.JwtTokenProvider;
import com.example.doktoribackend.user.domain.User;
import com.example.doktoribackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class KakaoAuthService implements OAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.oauth.frontend-redirect}")
    private String frontendRedirect;

    public String buildAuthorizeUrl(String state) {
        return kakaoOAuthClient.buildAuthorizeUrl(state);
    }

    @Override
    @Transactional
    public String handleCallback(String code) {
        KakaoTokenResponse tokenResponse = kakaoOAuthClient.exchangeToken(code);
        KakaoUserResponse userResponse = kakaoOAuthClient.fetchUser(tokenResponse.getAccessToken());

        Long kakaoId = userResponse.getId();
        String email = userResponse.getKakaoAccount() != null ? userResponse.getKakaoAccount().getEmail() : null;
        String rawNickname = userResponse.getKakaoAccount() != null && userResponse.getKakaoAccount().getProfile() != null
                ? userResponse.getKakaoAccount().getProfile().getNickname()
                : null;
        String nickname = (rawNickname == null || rawNickname.isBlank())
                ? "kakao-" + kakaoId
                : rawNickname;

        User user = userRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId)
                .orElseGet(() -> userRepository.findByEmailAndDeletedAtIsNull(email)
                        .orElseGet(() -> userRepository.save(User.createKakaoUser(kakaoId, email, nickname))));

        user.updateProfile(email, nickname);
        user.linkKakaoId(kakaoId);

        return jwtTokenProvider.createRefreshToken(user);

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
}
