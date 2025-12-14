package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;

public interface AttendanceService {
    AttendanceRecord checkIn(AttendanceRequest request);
    AttendanceRecord checkOut(AttendanceRecord record);
    boolean leaveTemporarily(AttendanceRequest request);
    boolean returnFromTemporarily(AttendanceRequest request);
    AttendanceRecord getAttendanceRecordByStudentId(AttendanceRequest request);
    AttendanceRecord getTodayCompletedAttendanceRecords(AttendanceRequest request);
}