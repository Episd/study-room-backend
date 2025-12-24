package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.exception.ServiceException;
import com.nbucs.studyroombackend.mapper.StudentMapper;
import com.nbucs.studyroombackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public StudentUser checkSelfInformation(StudentUser student) {
        return studentMapper.selectById(student.getStudentID());
    }

    @Override
    public boolean modifySelfInformation(StudentUser student) {
        if (student.getStudentID() == null) {
            throw new ServiceException(500, "学生ID为空");
        }
        int rows = studentMapper.updateById(student);
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean deductStudentPoints(Integer studentId, Integer points) {
        // 参数验证
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (points == null || points <= 0) {
            throw new IllegalArgumentException("扣除积分数必须大于0");
        }

        // 查询学生是否存在
        StudentUser student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查积分是否足够
        if (student.getStudentPoints() == null || student.getStudentPoints() < points) {
            throw new RuntimeException("学生积分不足");
        }

        // 计算扣除后的积分
        int newPoints = student.getStudentPoints() - points;

        // 更新学生积分
        UpdateWrapper<StudentUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("studentID", studentId)
                .set("studentPoints", newPoints);

        int result = studentMapper.update(null, updateWrapper);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean addStudentPoints(Integer studentId, Integer points) {
        // 参数验证
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (points == null || points <= 0) {
            throw new IllegalArgumentException("恢复积分数必须大于0");
        }

        // 查询学生是否存在
        StudentUser student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查积分是否足够
        if (student.getStudentPoints() == null || student.getStudentPoints() < points) {
            throw new RuntimeException("学生积分不足");
        }

        // 计算扣除后的积分
        int newPoints = student.getStudentPoints() + points;

        // 更新学生积分
        UpdateWrapper<StudentUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("studentID", studentId)
                .set("studentPoints", newPoints);

        int result = studentMapper.update(null, updateWrapper);
        return result > 0;
    }

    @Override
    @Transactional
    public int resetAllStudentsPoints() {
        // 创建更新包装器，更新所有学生
        UpdateWrapper<StudentUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("studentPoints", 100); // 设置积分为100

        // 执行更新
        int updatedCount = studentMapper.update(null, updateWrapper);

        return updatedCount;
    }
}
