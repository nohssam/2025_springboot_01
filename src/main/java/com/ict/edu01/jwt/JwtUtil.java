package com.ict.edu01.jwt;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    private final Key secretKey;
    private long accessTokenValidity;
    private long refreshTokenValidity;

    // 생성자
    public JwtUtil(String secret, long accessTokenValidity, long refreshTokenValidity){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // Access Token 생성
    public String generateAccessToken(String userId){
        return Jwts.builder()
               .setSubject(userId)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
               .signWith(secretKey,SignatureAlgorithm.HS256)
               .compact();
    }

    // Refresh Token 생성
     public String generateRefreshToken(String userId){
         return Jwts.builder()
               .setSubject(userId)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
               .signWith(secretKey,SignatureAlgorithm.HS256)
               .compact();
    }

    // Refresh Token 유효기간 반환
    public long getRefreshTokenValidity(){
        return refreshTokenValidity;
    }

    // JWT 검증 및 사용자 ID 추출
    public String validateAndExtractUserId(String token){
        try {
            // 넘어온 token 는 "Bearer Token내용"
            // Bearer 는 HTTP Authorization 헤더를 통해 토큰 전달 방식
            // Bearer 의미는  "이 토큰 소지한 자는 인증된 것으로 간주한다." 의미
            // Authorization: Bearer token
            token = token.substring(7); // token 값만 가져오기 
            Claims claims = Jwts.parserBuilder()  
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            return claims.getSubject();            
        } catch (JwtException | IllegalArgumentException e) {
           throw new IllegalArgumentException("Invalid Token");
        }
    }

}
