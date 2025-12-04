package com.nbucs.studyroombackend.service.impl;

import com.nbucs.studyroombackend.entity.Studentuser;
import com.nbucs.studyroombackend.mapper.StudentMapper;
import com.nbucs.studyroombackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public Studentuser checkSelfInformation(Studentuser student) {
        return studentMapper.selectById(student.getStudentId());
    }

    @Override
    public boolean modifySelfInformation(Studentuser student) {
        return false;
    }
}
