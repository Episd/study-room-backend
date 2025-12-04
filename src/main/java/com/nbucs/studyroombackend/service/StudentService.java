package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.Studentuser;

public interface StudentService {
    public Studentuser checkSelfInformation(Studentuser student);
    public boolean modifySelfInformation(Studentuser student);
}
