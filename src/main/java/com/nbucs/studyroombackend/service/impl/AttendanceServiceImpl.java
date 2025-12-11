package com.nbucs.studyroombackend.service.impl;


import com.nbucs.studyroombackend.dto.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.mapper.AttendanceRecordMapper;
import com.nbucs.studyroombackend.service.AttendanceService;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private final AttendanceRecordMapper attendanceRecordMapper;
    @Autowired
    private final SeatService seatService;

    public AttendanceServiceImpl(AttendanceRecordMapper attendanceRecordMapper,
                                 SeatService seatService) {
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.seatService = seatService;
    }

    @Override
    @Transactional
    public AttendanceRecord checkIn(AttendanceRequest request) {
        // 1. TODO：可在这里校验该学生当前时间是否有有效预约

        // 2. 更新座位状态为“已占用”
        if (request.getSeatId() != null) {
            Seat seat = seatService.getSeatById(request.getSeatId());
            seat.setSeatStatus(3);
            seatService.updateSeatStatus(seat.getSeatId(),seat.getSeatStatus());
        }

        // 3. 新建考勤记录
        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceRecordId(generateId());
        record.setStudentId(request.getStudentId());
        record.setCheckInTime(
                request.getOperateTime() != null ? request.getOperateTime() : LocalDateTime.now()
        );
        record.setAwayDuration(0);

        attendanceRecordMapper.insert(record);

        // 4. TODO：调用日志服务记录“签到”操作

        return record;
    }

    @Override
    @Transactional
    public boolean checkOut(AttendanceRequest request) {
        // 查出今天该学生最近一次考勤记录
//        LocalDate today = LocalDate.now();
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
//
//        AttendanceRecord record = attendanceRecordMapper
//                .selectLatestByStudentAndDay(request.getStudentId(), start, end);
//
//        if (record == null || record.getSignOutTime() != null) {
//            // 没有有效的“已签到未签退”记录
//            return false;
//        }
//
//        // 更新签退时间
//        LocalDateTime signOutTime =
//                request.getOperateTime() != null ? request.getOperateTime() : LocalDateTime.now();
//        record.setSignOutTime(signOutTime);
//        attendanceRecordMapper.updateById(record);
//
//        // 座位恢复为“可预约”
//        if (request.getSeatId() != null) {
//            Seat seat = seatService.checkSeatInformation(request.getSeatId());
//            // 假设 0 = 可预约
//            seat.setSeatStatus(0);
//            seatService.updateSeatStatus(seat);
//        }
        return true;
    }

    @Override
    @Transactional
    public boolean leaveTemporarily(AttendanceRequest request) {
//        LocalDate today = LocalDate.now();
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
//
//        AttendanceRecord record = attendanceRecordMapper
//                .selectLatestByStudentAndDay(request.getStudentId(), start, end);
//
//        if (record == null) {
//            return false;
//        }
//
//        // 累加暂离时长
//        Integer old = record.getAwayDuration() == null ? 0 : record.getAwayDuration();
//        Integer add = request.getDurationMinutes() == null ? 0 : request.getDurationMinutes();
//        record.setAwayDuration(old + add);
//        attendanceRecordMapper.updateById(record);
//
//        // 座位状态改为“暂离”
//        if (request.getSeatId() != null) {
//            Seat seat = seatService.checkSeatInformation(request.getSeatId());
//            seat.setSeatStatus(4);
//            seatService.updateSeatStatus(seat);
//        }
        return true;
    }

    private String generateId() {
        // 简单起见：取 UUID 前 20 位
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }
}