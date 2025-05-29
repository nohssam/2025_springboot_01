package com.ict.edu01.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ict.edu01.jwt.JwtRequestFilter;
import com.ict.edu01.jwt.JwtUtil;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.service.MyUserDetailService;

import lombok.extern.slf4j.Slf4j;


// Configuration : 설정 클래스 (Spring Boot 가 시작될때 실행 된다.)
@Slf4j
@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUtil jwtUtil;
    private final MyUserDetailService userDetailService;
    private final MembersService membersService;

    // 생성자
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, JwtUtil jwtUtil,
        MyUserDetailService userDetailService, MembersService membersService){
        log.info("SecurityConfig const");
         
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailService = userDetailService;
        this.membersService = membersService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain call");
        http
            // CORS 설정 ( a -> b 의미는  a 를 받아서 b를 실행하라 (람다식 표현))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF 보호 비활성화 (JWT 사용시 일반적으로 비활성화 )
            // 사용자가 로그인 된 상태를 악용하여, 악의적인 사이트가 사용자의 권한으로 요청을 보내도록 만드는 공격
            // JWT는 세션을 사용하지 않고, Authorization헤더로 인증(CSRF 의 위험이 없음)
            .csrf(csrf -> csrf.disable())

            // 요청별 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/members/login").permitAll()     // 허용하는 url만 작성
                .requestMatchers("/api/members/register").permitAll()   // 허용하는 url만 작성
                .anyRequest().authenticated())

            // oauth2Login 설정

            // 사용자 요청이 오면 먼저 jwtRequestFilter가 실행되어, JWT토큰을 검증 할 후
            // 이상이 없으면 SpringSecurity의 인증된 사용자로 처리된다.
            // UsernamePasswordAuthenticationFilter보다 앞에 삽입하라
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        // 원래 WebConfig.java에서 했던것 
        /*
        @Configuration
        public class WebConfig implements WebMvcConfigurer{
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // 모든 엔드포인트에 대한 CORS 허용
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowCredentials(true);  // 인증정보 포함함
                }
            }
        */
        CorsConfiguration corsConfig = new CorsConfiguration();
        // 허용할 Origin 설정, 메서드, 헤더, 인증증
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://studyjava.shop","http://43.203.201.193"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
        // WebConfig.java 삭제하자 
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자 등록
    // AuthenticationConfiguration : Spring Security가 자동으로 만들어주는 객체
    //                               UserDetailsService 와  PasswordEncoder 등을 포함하고 있는 객체 
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)throws Exception{
        return authConfig.getAuthenticationManager();
    }
}
