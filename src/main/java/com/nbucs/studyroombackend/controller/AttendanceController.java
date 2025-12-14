package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.dto.response.AttendanceResponse;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学生端考勤控制器，对应边界类 StudentAttendance
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }


    @PostMapping("/check-in")
    public Response<AttendanceResponse> checkIn(@RequestBody AttendanceRequest request) {
        System.out.println("收到签到请求：学生ID：" + request.getStudentId() + " 座位号：" + request.getSeatNumber() + " 房间号：" + request.getRoomId());
        // 调用 Service 完成签到逻辑

        AttendanceRecord record = attendanceService.checkIn(request);
        AttendanceResponse response = new AttendanceResponse(record.getAttendanceRecordId(), record.getCheckInTime());
        System.out.println("签到成功！");
        return Response.success("签到成功", response);
    }

    /**
     * 签退
     */
    @PutMapping("/check-out/{recordId}")
    public Response<AttendanceRecord> checkOut(@PathVariable Long recordId) {
        System.out.print("收到签退请求：预约ID：" + recordId);
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setAttendanceRecordId(recordId);
        attendanceRecord = attendanceService.checkOut(attendanceRecord);
        return Response.success("签退成功!", attendanceRecord);
    }

    /**
     * 暂离
     */
    @PutMapping("/temporary-leave/{recordId}")
    public Response<?> leaveTemporarily(@PathVariable Long recordId) {
        AttendanceRequest request = new AttendanceRequest();
        request.setRecordId(recordId);
        attendanceService.leaveTemporarily(request);
        return Response.success("暂离成功!", null);
    }
    /**
     * 返回暂离
     */
    @PutMapping("/return/{recordId}")
    public Response<?> returnFromTemporarily(@PathVariable Long recordId) {
        AttendanceRequest request = new AttendanceRequest();
        request.setRecordId(recordId);
        attendanceService.returnFromTemporarily(request);
        return Response.success("返回暂离成功!", null);
    }

    /**
     * 查询正在签到中的考勤记录
     */
    @GetMapping("/ongoing/{studentId}")
    public Response<AttendanceResponse> getOngoingAttendance(@PathVariable Integer studentId) {
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(studentId);

        AttendanceRecord record = attendanceService.getAttendanceRecordByStudentId(request);
        AttendanceResponse response = AttendanceResponse.fromRecord(record);
        return Response.success("查询成功", response);
    }

    /**
     * 查询当天已完成的签到记录
     */
    @GetMapping("/completed/{studentId}")
    public Response<AttendanceResponse> getTodayCompletedAttendance(@PathVariable Integer studentId) {
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(studentId);

        AttendanceRecord record = attendanceService.getTodayCompletedAttendanceRecords(request);
        AttendanceResponse response = AttendanceResponse.fromRecord(record);
        return Response.success("查询成功", response);
    }
}
