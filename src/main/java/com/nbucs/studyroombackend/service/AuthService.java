package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.Studentuser;

public interface AuthService {
    public boolean loginStudentByPhoneNumber(String phoneNumber, String password);
    public Studentuser loginStudentById(Integer id, String password);
    public Studentuser registerStudent(Studentuser student);
    public boolean resetPassword(Integer id, String password);
}
