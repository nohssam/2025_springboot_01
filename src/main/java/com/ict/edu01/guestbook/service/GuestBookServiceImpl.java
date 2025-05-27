package com.ict.edu01.guestbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ict.edu01.guestbook.mapper.GuestBookMapper;
import com.ict.edu01.guestbook.vo.GuestBookVO;

@Service
public class GuestBookServiceImpl implements GuestBookService{

    @Autowired
    private GuestBookMapper guestBookMapper ;
    @Override
    public List<GuestBookVO> guestbooklist() {
        return guestBookMapper.guestbooklist();
    }
    @Override
    public GuestBookVO guestbookdetail(String gb_idx) {
       return guestBookMapper.guestbookdetail(gb_idx);
    }
    
    
}
