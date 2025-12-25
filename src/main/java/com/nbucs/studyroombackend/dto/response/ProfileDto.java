package com.nbucs.studyroombackend.dto.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.nbucs.studyroombackend.entity.StudentUser;
import lombok.Data;

@Data
public class ProfileDto {
    private Integer studentID;
    private String studentName;
    private String studentCollege;
    private Integer studentPoints;
    private Integer studentGrade;
    private String studentPhoneNumber;
    private String studentUserName;
    public void transFrom(StudentUser user) {
        this.studentID = user.getStudentID();
        this.studentName = user.getStudentName();
        this.studentCollege = user.getStudentCollege();
        this.studentPoints = user.getStudentPoints();
        this.studentGrade = user.getStudentGrade();
        this.studentPhoneNumber = user.getStudentPhoneNumber();
        this.studentUserName = user.getStudentUserName();
    }
}
