package com.nbucs.studyroombackend.servicetest;


import com.nbucs.studyroombackend.dto.request.AttendanceRequest;
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

    }

    @Test
    void checkIn_shouldOnlyInsertRecord_whenSeatIdIsNull() {

    }

    @Test
    void checkOut_shouldUpdateRecordAndSeat_whenRecordExists() {

    }

    @Test
    void checkOut_shouldReturnFalse_whenNoRecord() {

    }

    @Test
    void leaveTemporarily_shouldIncreaseAwayDurationAndSetSeatStatus() {

    }

    @Test
    void leaveTemporarily_shouldReturnFalse_whenNoRecord() {

    }
}
