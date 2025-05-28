package com.ict.edu01.jwt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.vo.MembersVO;

@Service
public class JwtServiceImpl implements JwtService{

    @Autowired
    private MembersService membersService;

    @Autowired
    private JwtUtil jwtUtil;
    
    // @Autowired
    // private RefreshTokenMapper refreshTokenMapper;

    @Override
    public Map<String, String> login(MembersVO mvo) {
       // 사용자 확인
       MembersVO user = membersService.getLogin(mvo);
       System.out.println("user_id : " + user.getM_id()); 

       if(user == null){
            throw new IllegalArgumentException("Invalid Token");
       }

       // springSecurity

       // Refresh Token 처리

       // Access Token 생성
       String accessToken = jwtUtil.generateAccessToken(user.getM_id());
       
       // 결과 저장
       Map<String, String> tokens = new HashMap<>();
       tokens.put("accessToken", accessToken);
       // 원래는 refreshToken 도 저장

       return tokens;
    }

    @Override
    public String getuserIdFromToken(String token) {
       // 토큰으로 사용자 추출
       return jwtUtil.validateAndExtractUserId(token);
    }
    
}
