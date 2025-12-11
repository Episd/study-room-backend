package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Value;

@Data
@TableName("studentuser")
public class StudentUser {
    @TableId(value = "studentID")
    private Integer studentId;
    @TableField("studentName")
    private String studentName;
    @TableField("studentPassword")
    private String studentPassword;
    @TableField("studentCollege")
    private String studentCollege;
    @TableField("studentPoints")
    private Integer studentPoints;
    @TableField("studentGrade")
    private Integer studentGrade;
    @TableField("studentPhoneNumber")
    private String studentPhoneNumber;
    @TableField("studentUserName")
    private String studentUserName;
}
