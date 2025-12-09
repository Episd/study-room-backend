package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.request.AuthDto;
import com.nbucs.studyroombackend.dto.Response;
import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.service.AuthService;
import com.nbucs.studyroombackend.util.JwUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwUtil jwUtil;

        @PostMapping("/loginStudentById")
    public Response<?> loginStudentById(@RequestBody AuthDto loginForm) {
       Integer Id = loginForm.getId();
       String password = loginForm.getPassword();

       StudentUser studentUser = authService.loginStudentById(Id, password);

       String token = jwUtil.generateToken(String.valueOf(studentUser.getStudentId()));
       Map<String, Object> data = new HashMap<>();
       data.put("token", token);
       data.put("user", studentUser);
       return Response.success("登录成功", data);
    }

    @PostMapping("/registerStudent")
    public Response<?> registerStudent(@RequestBody AuthDto registerForm) {
        Integer Id = registerForm.getId();
        String password = registerForm.getPassword();

        System.out.println("收到注册请求：学号：" + Id + "，密码：" + password);

        StudentUser student = new StudentUser();
        student.setStudentPassword(password);
        student.setStudentId(registerForm.getId());
        student.setStudentCollege(registerForm.getCollege());
        student.setStudentName(registerForm.getName());
        student.setStudentPhoneNumber(registerForm.getPhone());
        student.setStudentGrade(Integer.valueOf(registerForm.getGrade()));

        StudentUser studentUser = authService.registerStudent(student);

        String token = jwUtil.generateToken(String.valueOf(studentUser.getStudentId()));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", studentUser);
        return Response.success("注册成功", data);
    }

    @PostMapping("/resetStudentPassword")
    public Response<?> resetPassword(@RequestBody AuthDto resetForm) {
        Integer id = resetForm.getId();
        String phone = resetForm.getPhone();
        String password = resetForm.getPassword();

        System.out.println("收到重置密码请求：学号：" + id + "，电话号码：" + phone + "，密码：" + password);

        authService.resetPassword(id, phone, password);

        return Response.success("重置密码成功", null);
    }
}
