package com.nbucs.studyroombackend.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.exception.ServiceException;
import com.nbucs.studyroombackend.mapper.AttendanceRecordMapper;
import com.nbucs.studyroombackend.mapper.ReservationRecordMapper;
import com.nbucs.studyroombackend.service.AttendanceService;
import com.nbucs.studyroombackend.service.ReservationService;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private final AttendanceRecordMapper attendanceRecordMapper;
    @Autowired
    private final ReservationService reservationService;
    @Autowired
    private final SeatService seatService;
    @Autowired
    private final ReservationRecordMapper reservationRecordMapper;

    public AttendanceServiceImpl(AttendanceRecordMapper attendanceRecordMapper, ReservationService reservationService,
                                 SeatService seatService, ReservationRecordMapper reservationRecordMapper) {
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.reservationService = reservationService;
        this.seatService = seatService;
        this.reservationRecordMapper = reservationRecordMapper;
    }

    @Override
    @Transactional
    public AttendanceRecord checkIn(AttendanceRequest request) {
        System.out.println("进行签到服务中：学生Id：" + request.getStudentId() + "，座位号：" + request.getSeatNumber() + "，教室号：" + request.getRoomId());
        // 1. 查找对应预约记录
        ReservationRecord reservation = reservationRecordMapper.selectOne(
                new QueryWrapper<ReservationRecord>()
                        .eq("studentID", request.getStudentId())
                        .eq("studyRoomID", request.getRoomId())
                        .eq("seatID", request.getSeatNumber())
                        .eq("reservationRecordStatus", 1)
                        .orderByDesc("reservationStartTime")
                        .last("LIMIT 1")
        );

        if (reservation == null) {
            throw new ServiceException(404, "未找到有效的预约记录，无法签到");
        }

        // 2. 生成考勤记录并补全信息
        AttendanceRecord record = new AttendanceRecord();
        record.setStudentId(request.getStudentId());
        record.setSeatId(request.getSeatNumber());
        record.setCheckInTime(LocalDateTime.now());
        record.setSignOutTime(null);
        record.setAwayDuration(0);
        record.setActualStudyDuration(0);

        // 补全预约相关信息
        record.setReservationRecordId(reservation.getReservationRecordId());
        record.setSeminarRoomId(reservation.getSeminarRoomId());

        // 初始考勤状态：1-正常
        record.setAttendanceStatus(1);

        // 3. 插入考勤记录
        attendanceRecordMapper.insert(record);

        seatService.updateSeatStatus(request.getSeatNumber(), 3);

        return record;
    }


    @Override
    @Transactional
    public AttendanceRecord checkOut(AttendanceRecord record) {
        System.out.println("进行签退服务中：签到记录Id：" + record.getAttendanceRecordId());
        // 查找签到记录
        AttendanceRecord attendanceRecord = attendanceRecordMapper.selectById(record.getAttendanceRecordId());
        if (attendanceRecord == null) {
            System.out.println("未找到签到记录，无法签退");
            throw new ServiceException(404, "未找到签到记录，无法签退");
        }

        // 设置签退时间
        LocalDateTime now = LocalDateTime.now();
        attendanceRecord.setSignOutTime(now);
        System.out.println("1111");
        // 计算实际学习时长（分钟）
        long totalMinutes = Duration.between(attendanceRecord.getCheckInTime(), now).toMinutes();
        int actualStudyDuration = (int) (totalMinutes - (attendanceRecord.getAwayDuration() == null ? 0 : attendanceRecord.getAwayDuration()));
        attendanceRecord.setActualStudyDuration(Math.max(actualStudyDuration, 0));
        System.out.println("实际学习时长：" + actualStudyDuration + "分钟");
        // TODO: 添加违规相关的处理，比如通过签退时间和预约时间比较来判断早退、超时等情况

        // 更新数据库
        if(attendanceRecordMapper.updateById(attendanceRecord) == 0) {
            throw new ServiceException(500, "更新数据库失败");
        }

        // 返回更新后的记录
        return attendanceRecord;
    }

    @Override
    @Transactional
    public boolean leaveTemporarily(AttendanceRequest request) {
        System.out.println("进行临时离开服务中：考勤记录Id：" + request.getRecordId());
        AttendanceRecord attendanceRecord = attendanceRecordMapper.selectById(request.getRecordId());
        attendanceRecord.setAwayStartTime(LocalDateTime.now());
        attendanceRecordMapper.updateById(attendanceRecord);
        return true;
    }

    @Override
    @Transactional
    public boolean returnFromTemporarily(AttendanceRequest request) {
        System.out.println("进行返回暂离服务中：考勤记录Id：" + request.getRecordId());
        AttendanceRecord attendanceRecord = attendanceRecordMapper.selectById(request.getRecordId());
        if (attendanceRecord == null) {
            throw new ServiceException(404, "考勤记录不存在，recordId=" + request.getRecordId());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime awayStart = attendanceRecord.getAwayStartTime();

        if (awayStart != null) {
            // 计算本次暂离时长
            int duration = Math.toIntExact(Duration.between(awayStart, now).toMinutes());

            // 累加到 awayDuration
            Integer currentDuration = attendanceRecord.getAwayDuration();
            if (currentDuration == null) {
                currentDuration = 0;
            }
            attendanceRecord.setAwayDuration((int) (currentDuration + duration));

            // 清空 awayStartTime，表示已返回
            attendanceRecord.setAwayStartTime(null);
        } else {
            throw new ServiceException(404, "考勤记录未记录暂离时间，无法返回");
        }

        attendanceRecordMapper.updateById(attendanceRecord);
        return true;
    }

    @Override
    @Transactional
    public AttendanceRecord getAttendanceRecordByStudentId(AttendanceRequest request) {
        // 根据 studentId 查询正在签到中的考勤记录
        return attendanceRecordMapper.selectOne(
                new QueryWrapper<AttendanceRecord>()
                        .eq("studentID", request.getStudentId())
                        .eq("attendanceStatus", 1)
        );
    }

    @Override
    @Transactional
    public AttendanceRecord getTodayCompletedAttendanceRecords(AttendanceRequest request) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // 查询当天已完成的签到记录
        return attendanceRecordMapper.selectOne(
                new QueryWrapper<AttendanceRecord>()
                        .eq("studentID", request.getStudentId())
                        .ge("checkInTime", startOfDay)
                        .lt("signOutTime", endOfDay)
                        .ne("attendanceStatus", 1)
        );
    }
}