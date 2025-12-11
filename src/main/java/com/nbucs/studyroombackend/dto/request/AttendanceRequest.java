package com.nbucs.studyroombackend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRequest {
    private int studentId;
    private String studyRoomId;
    private String seatId;
}
