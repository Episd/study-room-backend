package com.nbucs.studyroombackend.service.impl;

import com.nbucs.studyroombackend.entity.StudentUser;
import com.nbucs.studyroombackend.mapper.StudentMapper;
import com.nbucs.studyroombackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return false;
    }
}
