package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.dto.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;

public interface AttendanceService {
    /**
     * 学生签到：生成考勤记录，座位状态变为“已占用”
     */
    AttendanceRecord checkIn(AttendanceRequest request);

    /**
     * 学生签退：更新考勤记录签退时间，座位状态变为“可预约”
     */
    boolean checkOut(AttendanceRequest request);

    /**
     * 学生暂离：记录暂离时长，座位状态变更为“暂离”
     */
    boolean leaveTemporarily(AttendanceRequest request);
}