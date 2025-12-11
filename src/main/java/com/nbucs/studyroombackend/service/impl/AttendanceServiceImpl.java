package com.nbucs.studyroombackend.service.impl;


import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.mapper.AttendanceRecordMapper;
import com.nbucs.studyroombackend.service.AttendanceService;
import com.nbucs.studyroombackend.service.ReservationService;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private final AttendanceRecordMapper attendanceRecordMapper;
    @Autowired
    private final ReservationService reservationService;
    @Autowired
    private final SeatService seatService;

    public AttendanceServiceImpl(AttendanceRecordMapper attendanceRecordMapper, ReservationService reservationService,
                                 SeatService seatService) {
        this.attendanceRecordMapper = attendanceRecordMapper;
        this.reservationService = reservationService;
        this.seatService = seatService;
    }

    @Override
    @Transactional
    public boolean checkIn(AttendanceRequest request) {
        // 生成考勤记录
        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceRecordId(UUID.randomUUID().toString());
        record.setStudentId(request.getStudentId());
        record.setSeatId(request.getSeatId());
        record.setCheckInTime(LocalDateTime.now());
        record.setSignOutTime(null);
        record.setAwayDuration(0);
        record.setActualStudyDuration(0);
//        record.setReservationRecordId(reservationService.);

        attendanceRecordMapper.insert(record);
        return true;
    }

    @Override
    @Transactional
    public boolean checkOut(AttendanceRequest request) {

        return true;
    }

    @Override
    @Transactional
    public boolean leaveTemporarily(AttendanceRequest request) {

        return true;
    }
}