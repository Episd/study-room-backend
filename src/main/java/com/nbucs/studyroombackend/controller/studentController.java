package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.ProfileDto;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class studentController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/checkSelfInformation")
    public Response<ProfileDto> checkStudentByID(@RequestParam Integer studentID) {
        StudentUser user = new StudentUser();
        user.setStudentID(studentID);
        user = studentService.checkSelfInformation(user);
        ProfileDto result = new ProfileDto();
        result.transFrom(user);
        return Response.success("查询学生个人信息成功", result);
    }

    @PostMapping("/modifySelfInformation")
    public Response<Boolean> modifyStudentByID(@RequestBody ProfileDto modifiedUser) {
        StudentUser user = new StudentUser();
        user.setStudentID(modifiedUser.getStudentID());
        user.setStudentName(modifiedUser.getStudentName());
        user.setStudentCollege(modifiedUser.getStudentCollege());
        user.setStudentPoints(modifiedUser.getStudentPoints());
        user.setStudentGrade(modifiedUser.getStudentGrade());
        user.setStudentPhoneNumber(modifiedUser.getStudentPhoneNumber());
        user.setStudentUserName(modifiedUser.getStudentUserName());
        Boolean result = studentService.modifySelfInformation(user);
        return Response.success("修改学生个人信息成功", result);
    }
}
