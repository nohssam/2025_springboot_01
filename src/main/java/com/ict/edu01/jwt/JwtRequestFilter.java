package com.ict.edu01.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


// JWT 기반 인증을 처리하는 필터 
// HTTP 요청이 올때 마다 딱 한번 실행되며,  JWT 토근을 감시하고, 인증 처리 해줌줌
@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

   @Autowired
   private JwtUtil jwtUtil;
   
   @Autowired
   private UserDetailsService userDetailsService;


    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtRequestFilter call");

        // ✅ 토큰 검사 예외 처리: refresh 요청은 필터 통과
        String path = request.getRequestURI();
        if ("/api/members/refresh".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }
                
        // 들어오는 요청마다 Authorization 있고 Authorization를 jwt 검증하기 위해서 추출
        final String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwtToken = null;
        
        // authorizationHeader 에 "Bearer " 있어야 다음 단계를 할수 있다.
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwtToken = authorizationHeader.substring(7);
            try {
                // 토큰 만료 검사 
                if(jwtUtil.isTokenExpired(jwtToken)){
                    log.info("token expire error");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"token expired\"}");
                    return;
                }
                userId =  jwtUtil.validateAndExtractUserId(jwtToken);
                log.info("userId : " + userId);

            } catch (Exception e) {
                log.info("token error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"token error\"}");
                return;
            }

        }else{
            log.info("Authorization header is empty or doesn't start with Bearer");
        }

        // 사용자ID가 존재하고 SecurityContext에 인증정보가 없는 경우 등록하기 위해서서
        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            log.info("jwtToken-2 : " + jwtToken.substring(7));
            // 등록하자 
            // DB 에서 사용자 정보 가져오기 
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            
            // JWT 검증 및 SpringSecurity 인증객체에 사용자 정보를 등록
            if(jwtUtil.validateToken(jwtToken, userDetails)){
                // SpringSecurity 표준 인증 객체 (인증주체, 자격증명(null=jwt), 권한정보(ROLE))
                UsernamePasswordAuthenticationToken authToken =
                  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // SecurityContext에 등록
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JWT token ok");
            }else{
               log.info("JWT token error"); 
            }
            
        }

        // 필터 체인 실행 (다른 필터로 요청 전달)
        filterChain.doFilter(request, response);

    }
    
}
