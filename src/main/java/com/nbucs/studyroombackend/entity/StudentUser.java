package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("studentuser")
public class StudentUser {
    private Integer studentId;
    private String studentName;
    private String studentPassword;
    private String studentCollege;
    private Integer studentPoints;
    private Integer studentGrade;
    private String studentPhoneNumber;
    private String studentUserName;
}
