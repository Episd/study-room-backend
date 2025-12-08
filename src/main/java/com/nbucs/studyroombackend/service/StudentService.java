package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.StudentUser;

public interface StudentService {
    public StudentUser checkSelfInformation(StudentUser student);
    public boolean modifySelfInformation(StudentUser student);
}
