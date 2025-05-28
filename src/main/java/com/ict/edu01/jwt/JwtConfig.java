package com.ict.edu01.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    // 해당 값은 application.properties 에 존재해야 한다.
    @Value("${jwt.secret}")
    private String secret ;
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    @Bean
    public JwtUtil jwtUtil(){
        return new JwtUtil(secret, accessTokenValidity,refreshTokenValidity) ;
    }
}
