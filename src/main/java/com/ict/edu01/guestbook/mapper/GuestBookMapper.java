package com.ict.edu01.guestbook.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ict.edu01.guestbook.vo.GuestBookVO;

@Mapper
public interface GuestBookMapper {
    List<GuestBookVO> guestbooklist();
    GuestBookVO guestbookdetail(String gb_idx);
}
