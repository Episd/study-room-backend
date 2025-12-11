package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;

public interface AttendanceService {
    boolean checkIn(AttendanceRequest request);
    boolean checkOut(AttendanceRequest request);
    boolean leaveTemporarily(AttendanceRequest request);
}