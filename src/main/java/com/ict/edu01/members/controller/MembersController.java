package com.ict.edu01.members.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ict.edu01.members.vo.DataVO;
import com.ict.edu01.members.vo.MembersVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/members")
public class MembersController {
    
    @GetMapping("/hello")
    public String getHello() {
        return "Hello, SpringBoot";
    }

    @PostMapping("/login")
    public DataVO getLogin(@RequestBody MembersVO mvo) {
                
        return null;
    }
    
    
}