package com.ict.edu01.members.service;

import java.util.Date;

import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

public interface MembersService {
    MembersVO getLogin(MembersVO mvo);
    int getRegister(MembersVO mvo);
    MembersVO getMyPage(String m_id);
    void saveRefreshToken(String m_id, String refreshToken, Date expiry_date);
    RefreshVO getRefreshToken(String m_id);      
} 
