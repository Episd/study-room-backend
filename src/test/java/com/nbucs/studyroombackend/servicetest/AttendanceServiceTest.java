package com.nbucs.studyroombackend.servicetest;


import com.nbucs.studyroombackend.dto.AttendanceRequest;
import com.nbucs.studyroombackend.entity.AttendanceRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.mapper.AttendanceRecordMapper;
import com.nbucs.studyroombackend.service.SeatService;
import com.nbucs.studyroombackend.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {
    @Mock
    private AttendanceRecordMapper attendanceRecordMapper;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Test
    void checkIn_shouldInsertRecordAndUpdateSeat_whenSeatIdProvided() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(1);
        request.setSeatId("S001");
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 9, 0);
        request.setOperateTime(now);

        Seat seat = new Seat();
        seat.setSeatId("S001");
        seat.setSeatStatus(0);

        when(seatService.checkSeatInformation("S001")).thenReturn(seat);
        // insert 返回行数，这里随便给个 1
        when(attendanceRecordMapper.insert(any(AttendanceRecord.class))).thenReturn(1);

        // when
        AttendanceRecord result = attendanceService.checkIn(request);

        // then
        assertNotNull(result.getAttendanceRecordId());
        assertEquals(1, result.getStudentId());
        assertEquals(now, result.getCheckInTime());
        assertEquals(0, result.getAwayDuration());

        // 座位被设置为“已占用”
        assertEquals(3, seat.getSeatStatus());
        verify(seatService).checkSeatInformation("S001");
        verify(seatService).updateSeatStatus(seat);

        // 插入考勤记录被调用
        verify(attendanceRecordMapper).insert(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_shouldOnlyInsertRecord_whenSeatIdIsNull() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(2);
        request.setSeatId(null);
        request.setOperateTime(LocalDateTime.of(2025, 1, 1, 10, 0));

        when(attendanceRecordMapper.insert(any(AttendanceRecord.class))).thenReturn(1);

        // when
        AttendanceRecord result = attendanceService.checkIn(request);

        // then
        assertEquals(2, result.getStudentId());
        verify(attendanceRecordMapper).insert(any(AttendanceRecord.class));
        // 不应该操作座位
        verifyNoInteractions(seatService);
    }

    @Test
    void checkOut_shouldUpdateRecordAndSeat_whenRecordExists() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(1);
        request.setSeatId("S001");
        LocalDateTime signOutTime = LocalDateTime.of(2025, 1, 1, 11, 0);
        request.setOperateTime(signOutTime);

        AttendanceRecord existing = new AttendanceRecord();
        existing.setAttendanceRecordId("AR001");
        existing.setStudentId(1);
        existing.setCheckInTime(LocalDateTime.of(2025, 1, 1, 9, 0));
        existing.setSignOutTime(null);
        existing.setAwayDuration(0);

        when(attendanceRecordMapper.selectLatestByStudentAndDay(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(existing);

        Seat seat = new Seat();
        seat.setSeatId("S001");
        seat.setSeatStatus(2); // 已占用
        when(seatService.checkSeatInformation("S001")).thenReturn(seat);

        when(attendanceRecordMapper.updateById(existing)).thenReturn(1);

        // when
        boolean success = attendanceService.checkOut(request);

        // then
        assertTrue(success);
        assertEquals(signOutTime, existing.getSignOutTime());
        // 座位恢复为可预约
        assertEquals(0, seat.getSeatStatus());

        verify(attendanceRecordMapper).updateById(existing);
        verify(seatService).updateSeatStatus(seat);
    }

    @Test
    void checkOut_shouldReturnFalse_whenNoRecord() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(1);
        request.setSeatId("S001");

        when(attendanceRecordMapper.selectLatestByStudentAndDay(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);

        // when
        boolean success = attendanceService.checkOut(request);

        // then
        assertFalse(success);
        verify(attendanceRecordMapper, never()).updateById(any());
        verify(seatService, never()).updateSeatStatus(any());
    }

    @Test
    void leaveTemporarily_shouldIncreaseAwayDurationAndSetSeatStatus() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(1);
        request.setSeatId("S001");
        request.setDurationMinutes(30);

        AttendanceRecord existing = new AttendanceRecord();
        existing.setAttendanceRecordId("AR001");
        existing.setStudentId(1);
        existing.setAwayDuration(10); // 之前已经暂离 10 分钟

        when(attendanceRecordMapper.selectLatestByStudentAndDay(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(existing);

        Seat seat = new Seat();
        seat.setSeatId("S001");
        seat.setSeatStatus(2);
        when(seatService.checkSeatInformation("S001")).thenReturn(seat);

        when(attendanceRecordMapper.updateById(existing)).thenReturn(1);

        // when
        boolean success = attendanceService.leaveTemporarily(request);

        // then
        assertTrue(success);
        assertEquals(40, existing.getAwayDuration()); // 10 + 30

        // 座位状态变为暂离
        assertEquals(3, seat.getSeatStatus());
        verify(attendanceRecordMapper).updateById(existing);
        verify(seatService).updateSeatStatus(seat);
    }

    @Test
    void leaveTemporarily_shouldReturnFalse_whenNoRecord() {
        // given
        AttendanceRequest request = new AttendanceRequest();
        request.setStudentId(1);
        request.setSeatId("S001");
        request.setDurationMinutes(20);

        when(attendanceRecordMapper.selectLatestByStudentAndDay(
                eq(1), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(null);

        // when
        boolean success = attendanceService.leaveTemporarily(request);

        // then
        assertFalse(success);
        verify(attendanceRecordMapper, never()).updateById(any());
        verify(seatService, never()).updateSeatStatus(any());
    }
}
