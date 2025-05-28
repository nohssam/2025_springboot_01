package com.ict.edu01.jwt;

import java.util.Map;

import com.ict.edu01.members.vo.MembersVO;

public interface JwtService {
    Map<String,String> login(MembersVO mvo);  // 로그인 처리
    String getuserIdFromToken(String token);  // 토큰에서 사용자ID 추출
}
