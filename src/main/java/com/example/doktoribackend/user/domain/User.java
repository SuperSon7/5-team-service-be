package com.example.doktoribackend.user.domain;

import com.example.doktoribackend.common.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Instant deletedAt;

    public static User createKakaoUser(Long kakaoId, String email, String nickname) {
        return User.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .build();
    }

    public void updateProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void linkKakaoId(Long kakaoId) {
        if (this.kakaoId == null) {
            this.kakaoId = kakaoId;
        }
    }
}
