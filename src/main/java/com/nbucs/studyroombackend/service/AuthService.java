package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.AdminUser;
import com.nbucs.studyroombackend.entity.StudentUser;

public interface AuthService {
    public boolean loginStudentByPhoneNumber(String phoneNumber, String password);
    public StudentUser loginStudentById(Integer id, String password);
    public StudentUser registerStudent(StudentUser student);
    public boolean resetPassword(Integer id, String phone, String password);
    public AdminUser loginAdminById(Integer id, String password);
    public AdminUser registerAdmin(AdminUser admin);
    public boolean resetAdminPassword(Integer id, String phone, String password);
}
