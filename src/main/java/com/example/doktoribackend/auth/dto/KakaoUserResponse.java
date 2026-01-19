package com.example.doktoribackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserResponse {
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        private Profile profile;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("birthyear")
        private String birthyear;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}
