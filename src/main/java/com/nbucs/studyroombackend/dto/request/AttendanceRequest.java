package com.nbucs.studyroombackend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRequest {
    private int studentID;
    private Long studyRoomID;
    private Long seatID;
    private Long recordID;
}
