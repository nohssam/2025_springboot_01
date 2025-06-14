package com.ict.edu01.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.ict.edu01.jwt.JwtUtil;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.service.MyUserDetailService;
import com.ict.edu01.members.vo.MembersVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
// 소셜 로그인 성공 -> 사용자 정보 추출 -> DB저장 및 확인 -> jwt생성 -> React로 리다이렉트트
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
   private final JwtUtil jwtUtil;
   private final MyUserDetailService userDetailService;
   private final MembersService membersService;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, 
                MyUserDetailService userDetailService,MembersService membersService){
        this.jwtUtil = jwtUtil;
        this. userDetailService = userDetailService;
        this.membersService = membersService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            log.info(("소셜 로그인 성공 : OAuth2AuthenticationSuccessHandler "));

            // 현재 인증객체가 OAuth2 기반으로 인증 되었는지 확인하는 코드
            // OAuth2 (IETF 에서 개발된 공개 표준 인증 프로토콜)
            if(authentication instanceof OAuth2AuthenticationToken) {
               OAuth2AuthenticationToken oAuth2AuthenticationToken  = (OAuth2AuthenticationToken)authentication ;
               String provider = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
               log.info("provider : " + provider);

               // CustomerOAuth2UserService 에서 저장한 정보 호출
               OAuth2User oAuth2User =  oAuth2AuthenticationToken.getPrincipal();
               String id = oAuth2User.getAttribute("id");
               String name = oAuth2User.getAttribute("nickname");
               String email = oAuth2User.getAttribute("email");
               String token = jwtUtil.generateAccessToken(id);

               log.info("id : " + id);
               log.info("name : " + name);
               log.info("email : " + email);
               log.info("token : " + token);
               
               // DB에 저장
               MembersVO existing = membersService.getMyPage(id);
               if(existing == null){
                 MembersVO mvo = new MembersVO();
                 mvo.setM_id(id);
                 mvo.setM_name(name);
                 // 나중에 kakao와 naver 분리
                 mvo.setSns_email_kakao(email);
                 mvo.setSns_provider("kakao");

                 membersService.getRegister2(mvo);
                 log.info("신규 카카오 사용자 DB 에 저장장");
                 
               }

               // 쿠키에 token 저장
               Cookie cookie = new Cookie("authToken", token);
               cookie.setHttpOnly(false); // js에서 접근 불가
               cookie.setSecure(false);       // https에서만 사용 가능 (나중에 true)
               cookie.setPath("/");            // 전체 도메인에서 사용 가능
               response.addCookie(cookie);

               // 쿠키에 token 저장
               Cookie providerCookie = new Cookie("snsProvider", provider);
               providerCookie.setPath("/");            // 전체 도메인에서 사용 가능
               
               response.addCookie(providerCookie);
               
               // 쿠키를 jwt에서 처리 하자 
               response.sendRedirect("http://43.201.105.80/oauth2/redirect");    

            }
        } catch (Exception e) {
           log.info("error : " + e);
           response.sendRedirect("http://43.201.105.80/login");
        }
    }

    

}
