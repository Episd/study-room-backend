package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.AdminUser;
import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.exception.ServiceException;
import com.nbucs.studyroombackend.mapper.AdminMapper;
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
    private AdminMapper adminMapper;
    @Autowired
    private EncoderUtil passwordEncoderUtil;
    @Override
    public boolean loginStudentByPhoneNumber(String phoneNumber, String password) {
        return false;
    }

/**
 * 通过用户名进行学生登录验证
 * @param id 学号
 * @param password 密码
 * @return 登录成功返回true
 * @throws ServiceException 当用户不存在或密码错误时抛出异常
 */
    @Override
    public StudentUser loginStudentById(Integer id, String password) {
    // 创建查询条件包装器，设置查询用户名等于传入的用户名
        QueryWrapper<StudentUser> wrapper = new QueryWrapper<>();
        wrapper.eq("studentID", id);

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
        wrapper.eq("studentId", student.getStudentID());

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
        wrapper.eq("studentPhoneNumber", phone);

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

    @Override
    public AdminUser loginAdminById(Integer id, String password) {
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("adminID", id);

        AdminUser adminUser = adminMapper.selectOne(wrapper);
        System.out.println("管理员密码：" + adminUser.getAdminPassword());
        // 用户不存在 → 400
        if (adminUser == null) {
            throw new ServiceException(400, "用户不存在");
        }

        // 密码校验使用 BCrypt → 401
        // 使用BCrypt密码编码器验证密码是否匹配
        if (!passwordEncoderUtil.matches(password, adminUser.getAdminPassword())) {
            throw new ServiceException(401, "密码错误");
        }

        // 密码验证通过
        return adminUser;
    }

    @Override
    public AdminUser registerAdmin(AdminUser admin) {
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("adminId", admin.getAdminID());

        AdminUser existUser = adminMapper.selectOne(wrapper);

        if (existUser != null) {
            throw new ServiceException(409, "用户已注册");
        }

        String encodedPwd = passwordEncoderUtil.encode(admin.getAdminPassword());
        admin.setAdminPassword(encodedPwd);

        adminMapper.insert(admin);
        return admin;
    }

    @Override
    public boolean resetAdminPassword(Integer id, String phone, String password) {
        // 创建查询条件包装器，设置查询条件为管理员ID等于传入的Id
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("adminId", id);
        wrapper.eq("adminPhoneNumber", phone);

        // 根据条件查询管理员用户信息
        AdminUser adminUser = adminMapper.selectOne(wrapper);

        // 如果查询结果为空，则抛出用户不存在的异常
        if (adminUser == null) {
            throw new ServiceException(400, "用户不存在或电话号码错误");
        }

        // 对新密码进行加密编码
        String encodedPwd = passwordEncoderUtil.encode(password);
        // 设置加密后的密码到管理员用户对象
        adminUser.setAdminPassword(encodedPwd);

        // 更新管理员用户信息并返回更新是否成功
        return adminMapper.updateById(adminUser) > 0;
    }



}
