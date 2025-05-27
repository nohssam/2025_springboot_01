package com.ict.edu01.guestbook.service;

import java.util.List;

import com.ict.edu01.guestbook.vo.GuestBookVO;

public interface GuestBookService {
      List<GuestBookVO> guestbooklist();
       GuestBookVO guestbookdetail(String gb_idx);
}
