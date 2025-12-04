package com.nbucs.studyroombackend.dto.request;

import lombok.Data;

@Data
public class AuthDto {
    private String password;
    private Integer Id;
    private String name;
    private String phone;
    private String college;
    private String grade;
}
