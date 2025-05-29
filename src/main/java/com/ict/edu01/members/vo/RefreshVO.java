package com.ict.edu01.members.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshVO {
    private String  m_id, refresh_token;
    private Date expiry_date;
}
