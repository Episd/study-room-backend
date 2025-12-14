package com.nbucs.studyroombackend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceResponse {
    private String id;
    private LocalDateTime checkInTime;

    public AttendanceResponse() {
        ;
    }
    public AttendanceResponse(String id, LocalDateTime checkInTime) {
        this.id = id;
        this.checkInTime = checkInTime;
    }
}
