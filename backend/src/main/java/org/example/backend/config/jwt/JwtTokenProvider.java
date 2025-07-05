package org.example.backend.config.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;                      // JWT를 암호화/복호화할 때 쓰이는 키
    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidity;               // access 토큰의 유효시간
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidity;              // refresh 토큰의 유효시간

    private Key key;

    @PostConstruct
    protected void init(){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(Long userid){
        return createToken(userid, accessTokenValidity);
    }

    public String createRefreshToken(Long userid){
        return createToken(userid, refreshTokenValidity);
    }

    private String createToken(Long userId, Long validity){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // payload에 userId 넣음
                .setIssuedAt(now)                     // 발급 시각
                .setExpiration(expiry)                // 만료 시각
                .signWith(key, SignatureAlgorithm.HS256) // 시크릿 키 + 알고리즘
                .compact();                           // 최종 JWT 문자열 생성
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserId(String token){
        return Long.parseLong(
                Jwts.parserBuilder().setSigningKey(key).build()
                        .parseClaimsJws(token).getBody().getSubject()
        );
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);          //여기서 Exception 나면 유효하지 않다는 뜻
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }

    }

    // AccessToken 만료시간 추출
    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

}
