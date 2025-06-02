package com.ict.edu01.members.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ict.edu01.jwt.JwtService;
import com.ict.edu01.jwt.JwtUtil;
import com.ict.edu01.members.service.MembersService;
import com.ict.edu01.members.service.MyUserDetailService;
import com.ict.edu01.members.vo.DataVO;
import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/api/members")
public class MembersController {

    @Autowired
    private MembersService membersService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MyUserDetailService userDetailService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String getHello() {
        return "Hello, SpringBoot";
    }

    @PostMapping("/login")
    public DataVO getLogin(@RequestBody MembersVO mvo) {

        DataVO dataVO = new DataVO();

        try{

        // DataVO dataVO = new DataVO();
        // 만약에 맞면 
        // dataVO.setSuccess(true);
        // dataVO.setMessage("로그인 성공");
        
        // 만약 정보 전달할 data가 하나이면  
        // dataVO.setData(정보);
        
        // 만약 정보 전달할 data가 여러개 이면 
        // Map<String,Object> result = new HashMap<>();
        // result.put("list", list);
        // result.put("membersVO", mvo);
        // result.put("totalCount", totalCount);

        // 맞지 않으면 
        // dataVO.setSuccess(false);
        // dataVO.setMessage("로그인 실패");
        
        // DB 에 가서 m_id 와 m_pw가 맞는지 확인한다.
        /* jwt 사용하지 않은 로그인
        MembersVO membersVO = membersService.getLogin(mvo);
        if(membersVO == null){
            dataVO.setSuccess(false);
            dataVO.setMessage("로그인 실패");
        }else{
            // 로그인 성공했을 때 token 처리
            
            dataVO.setSuccess(true);
            dataVO.setMessage("로그인 성공");
            dataVO.setData(membersVO);
        }
        */
        
        // jwt를 활용한 로그인 처리 (암호화 해서 없어진다.)
        // Map<String, String> tokens = jwtService.login(mvo);

            UserDetails userDetails = userDetailService.loadUserByUsername(mvo.getM_id());
            if(! passwordEncoder.matches(mvo.getM_pw(), userDetails.getPassword())){
                return new DataVO(false, "비밀번호 틀림", null);
            }

            // 비밀번호가 맞으면 id가지고 accesstoken, refreshToken
            String accessToken =  jwtUtil.generateAccessToken(mvo.getM_id());
            String refreshToken = jwtUtil.generateRefreshToken(mvo.getM_id());

            // refreshToken DB에 저장해댜 된다.
            membersService.saveRefreshToken(mvo.getM_id(), refreshToken, jwtUtil.extractExpiration(refreshToken));

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            dataVO.setSuccess(true);
            dataVO.setData(tokens);
            dataVO.setMessage("로그인 성공");
        }catch(Exception e){
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류 : " + e.getMessage());
        }

        return dataVO;
    }
    
    @PostMapping("/register")
    public DataVO getRegister(@RequestBody MembersVO mvo) {
        DataVO dataVO = new DataVO();
        try {
            // 비번 암호화
            mvo.setM_pw(passwordEncoder.encode(mvo.getM_pw()));;

            int result = membersService.getRegister(mvo);
            if(result >0){
                dataVO.setSuccess(true);
                dataVO.setMessage("회원가입 성공");
            }else{
                dataVO.setSuccess(false);
                dataVO.setMessage("회원가입 실패");
            }

        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류 : " + e.getMessage());
        }
        return dataVO;
    }
    
    @GetMapping("/mypage")
    public DataVO getMyPage(HttpServletRequest request) {
        DataVO dataVO = new DataVO();
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            String m_id = jwtUtil.validateAndExtractUserId(token);
            MembersVO mvo = membersService.getMyPage(m_id);
            if(mvo == null){
                dataVO.setSuccess(false);
                dataVO.setMessage("잘못된 정보 입니다.");
            }else{
                dataVO.setSuccess(true);
                dataVO.setMessage("가져오기 성공");
                dataVO.setData(mvo);
            }
        } catch (Exception e) {
            dataVO.setSuccess(false);
            dataVO.setMessage("서버 오류 : " + e.getMessage());
        }
        return dataVO;
    }
    @PostMapping("/refresh")
    public DataVO getRefresh(@RequestBody Map<String, String> map) {
        try {
            log.info("refresh 들어왔네요");
            String refreshToken = map.get("refreshToken");
            
            // 1. 만료 여부 검사
            if(jwtUtil.isTokenExpired(refreshToken)){
                return new DataVO(false, "refreshToken 만료", null);
            }

            // 2. 사용자 ID 추출
            String m_id = jwtUtil.validateAndExtractUserId(refreshToken);

            // DB에 m_id 가지고 refresh token 을 확인(체크)
            RefreshVO refreshVO = membersService.getRefreshToken(m_id);
            
            // DB의 refreshToken과 유저가 보낸  refreshToken이 같아야 accessToken 발급
            if(refreshVO == null || ! refreshToken.equals((refreshVO.getRefresh_token()))) {
                 return new DataVO(false,"refreshToken 불일치", null);
            }
            
            // 새로운 accessToken, refreshToken 발급
             String newAccessToken = jwtUtil.generateAccessToken(m_id);
             String newRefreshToken = jwtUtil.generateRefreshToken(m_id);

             // newRefreshToken을 DB에 갱신하자
             membersService.saveRefreshToken(m_id, newRefreshToken, jwtUtil.extractExpiration(newRefreshToken));

            //  Map<String,String> map2 = new HashMap();
            //  map2.put("accessToken", newAccessToken);
            //  map2.put("refreshToken", newRefreshToken);
            //  return new DataVO(true, "재발급 성공" , map2);

             return new DataVO(true, "재발급 성공" , Map.of(
                "accessToken",newAccessToken,
                "refreshToken",newRefreshToken
             ));
        } catch (Exception e) {
            return new DataVO(false,"재발급 실패", null);
        }
    }
    
    
}