package com.nbucs.studyroombackend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRequest {
    private int studentId;
    private Long roomId;
    private Long seatNumber;
    private Long recordId;
}
