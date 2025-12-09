package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端考勤控制器，对应边界类 StudentAttendance
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * 签到
     */
    @PostMapping("/check-in")
    public AttendanceRecord checkIn(@RequestBody AttendanceRequest request) {
        return attendanceService.checkIn(request);
    }

    /**
     * 签退
     */
    @PostMapping("/check-out")
    public boolean checkOut(@RequestBody AttendanceRequest request) {
        return attendanceService.checkOut(request);
    }

    /**
     * 暂离
     */
    @PostMapping("/leave")
    public boolean leaveTemporarily(@RequestBody AttendanceRequest request) {
        return attendanceService.leaveTemporarily(request);
    }
}
