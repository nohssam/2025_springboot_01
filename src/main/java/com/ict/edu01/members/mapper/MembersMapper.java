package com.ict.edu01.members.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ict.edu01.members.vo.MembersVO;

@Mapper
public interface MembersMapper {
    MembersVO getLogin(MembersVO mvo);
}
