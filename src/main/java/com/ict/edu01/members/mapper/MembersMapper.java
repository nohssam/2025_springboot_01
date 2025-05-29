package com.ict.edu01.members.mapper;


import org.apache.ibatis.annotations.Mapper;
import com.ict.edu01.members.vo.MembersVO;
import com.ict.edu01.members.vo.RefreshVO;

@Mapper
public interface MembersMapper {
    
    MembersVO getLogin(MembersVO mvo);
    
    int getRegister(MembersVO mvo);
    MembersVO getMyPage(String m_idx);

    MembersVO findUserById(String m_id) ;

    void saveRefreshToken(RefreshVO refreshVO);
}