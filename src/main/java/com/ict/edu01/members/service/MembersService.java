package com.ict.edu01.members.service;

import com.ict.edu01.members.vo.MembersVO;

public interface MembersService {
    MembersVO getLogin(MembersVO mvo);
    int getRegister(MembersVO mvo);
    MembersVO getMyPage(String m_idx);
} 
