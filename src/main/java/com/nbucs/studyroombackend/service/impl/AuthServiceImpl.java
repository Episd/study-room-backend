package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.exception.ServiceException;
import com.nbucs.studyroombackend.mapper.StudentMapper;
import com.nbucs.studyroombackend.service.AuthService;
import com.nbucs.studyroombackend.util.EncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private EncoderUtil passwordEncoderUtil;
    @Override
    public boolean loginStudentByPhoneNumber(String phoneNumber, String password) {
        return false;
    }

/**
 * 通过用户名进行学生登录验证
 * @param Id 学号
 * @param password 密码
 * @return 登录成功返回true
 * @throws ServiceException 当用户不存在或密码错误时抛出异常
 */
    @Override
    public StudentUser loginStudentById(Integer Id, String password) {
    // 创建查询条件包装器，设置查询用户名等于传入的用户名
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("studentID", Id);

    // 根据条件查询学生用户信息
        StudentUser studentUser = studentMapper.selectOne(wrapper);

        // 用户不存在 → 400
        if (studentUser == null) {
            throw new ServiceException(400, "用户不存在");
        }

        // 密码校验使用 BCrypt → 401
    // 使用BCrypt密码编码器验证密码是否匹配
        if (!passwordEncoderUtil.matches(password, studentUser.getStudentPassword())) {
            throw new ServiceException(401, "密码错误");
        }

    // 密码验证通过
        return studentUser;
    }

    @Override
    public StudentUser registerStudent(StudentUser student) {
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("studentId", student.getStudentId());

        StudentUser existUser = studentMapper.selectOne(wrapper);

        if (existUser != null) {
            throw new ServiceException(409, "用户已注册");
        }

        String encodedPwd = passwordEncoderUtil.encode(student.getStudentPassword());
        student.setStudentPassword(encodedPwd);

        studentMapper.insert(student);
        return student;
    }

/**
 * 重置学生密码方法
 * @param id 学生ID
 * @param phone 学生电话号码
 * @param password 新密码
 * @return boolean 更新是否成功
 */
    @Override
    public boolean resetPassword(Integer id, String phone, String password) {
    // 创建查询条件包装器，设置查询条件为学生ID等于传入的Id
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("studentId", id);
        wrapper.eq("studentPhone", phone);

    // 根据条件查询学生用户信息
        StudentUser studentUser = studentMapper.selectOne(wrapper);
    // 如果查询结果为空，则抛出用户不存在的异常
        if (studentUser == null) {
            throw new ServiceException(400, "用户不存在或电话号码错误");
        }

    // 对新密码进行加密编码
        String encodedPwd = passwordEncoderUtil.encode(password);
    // 设置加密后的密码到学生用户对象
        studentUser.setStudentPassword(encodedPwd);

    // 更新学生用户信息并返回更新是否成功
        return studentMapper.updateById(studentUser) > 0;
    }
}
