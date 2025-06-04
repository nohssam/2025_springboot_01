package com.ict.edu01.config;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerOAuth2UserService extends DefaultOAuth2UserService{
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomerOAuth2UserService 호출");

       // 부모 클래스의 loadUser 메서드를 호출하여 기본 사용자 정보를 가져온다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 속성 가져오기 
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 어떤 제공자 인지 
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("kakao")){
            Map<String,Object> kakaoAccount = (Map<String,Object>)attributes.get("kakao_account");
            
            if(kakaoAccount == null){
                throw new OAuth2AuthenticationException("kakao error");
            }

            String email = (String) kakaoAccount.get("email");

            Map<String,Object> properties = (Map<String,Object>)attributes.get("properties");
            if(properties == null){
                throw new OAuth2AuthenticationException("kakao error");
            }
            String nickname = (String) properties.get("nickname");

            // 이 아이디는 카카오의 내 ID가 아니라 카카오 디벨로퍼에서 발급하는 ID
            String id = String.valueOf(attributes.get("id"));

            log.info("카카오 id {}", id);
            log.info("카카오 email {}", email);
            log.info("카카오 nickname {}", nickname);

            return new DefaultOAuth2User(oAuth2User.getAuthorities(), Map.of(
                "id", id,
                "email", email,
                "nickname", nickname
            ), "email");

        }else if(provider.equals("naver")){

        }else if(provider.equals("google")){

        }
        return oAuth2User;
    }

    
    
}
