package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("studentuser")
public class StudentUser {
    @TableId(value = "studentID", type = IdType.INPUT)
    private Integer studentId;
    private String studentName;
    private String studentPassword;
    private String studentCollege;
    private Integer studentPoints;
    private Integer studentGrade;
    private String studentPhoneNumber;
    private String studentUserName;
}
