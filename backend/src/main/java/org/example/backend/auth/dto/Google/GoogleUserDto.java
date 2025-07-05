package org.example.backend.auth.dto.Google;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleUserDto {

    private String id;       // 구글 고유 ID (sub)
    private String email;     // 이메일 주소
    private String name;      // 유저 이름
    private String picture;   // 프로필 사진 URL

    public GoogleUserDto(JsonNode json) {
        this.id = json.path("sub").asText();
        this.email = json.path("email").asText(null);
        this.name = json.path("name").asText(null);
        this.picture = json.path("picture").asText(null);
    }

}
