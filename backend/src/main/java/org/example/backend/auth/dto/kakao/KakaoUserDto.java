package org.example.backend.auth.dto.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class KakaoUserDto {

    private final String id;                // 카카오 고유 ID
    private final String email;            // 이메일 (nullable)
    private final String nickname;         // 닉네임
    private final String profileImageUrl;  // 프로필 이미지 URL

    public KakaoUserDto(JsonNode json) {            // JsonNode = “JSON 데이터 구조를 트리(Tree) 형태로 다루는 객체”
        this.id = json.path("id").asText();
        this.email = json.path("kakao_account").path("email").asText(null);
        this.nickname = json.path("properties").path("nickname").asText(null);
        this.profileImageUrl = json.path("kakao_account")
                .path("profile")
                .path("profile_image_url")
                .asText(null);
    }

}
