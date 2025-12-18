package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;

import java.util.List;

public interface AttendanceService {
    AttendanceRecord checkIn(AttendanceRequest request);
    AttendanceRecord checkOut(AttendanceRecord record);
    AttendanceRecord leaveTemporarily(AttendanceRequest request);
    AttendanceRecord returnFromTemporarily(AttendanceRequest request);
    AttendanceRecord getAttendanceRecordByStudentId(AttendanceRequest request);
    List<AttendanceRecord> getTodayCompletedAttendanceRecords(AttendanceRequest request);
}