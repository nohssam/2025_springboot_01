package com.ict.edu01.members.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ict.edu01.members.mapper.MembersMapper;
import com.ict.edu01.members.vo.MembersVO;

@Service
public class MembersServiceImpl implements MembersService {
    @Autowired
    private MembersMapper membersMapper;

    @Override
    public MembersVO getLogin(MembersVO mvo) {
        return membersMapper.getLogin(mvo);
    }

    @Override
    public int getRegister(MembersVO mvo) {
        return membersMapper.getRegister(mvo);
    }

    @Override
    public MembersVO getMyPage(String m_idx) {
       return membersMapper.getMyPage(m_idx);
    }

    
    
}
