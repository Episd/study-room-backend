package com.nbucs.studyroombackend.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReserveSeatFormDto {
    private Integer studentId;
    private String studyRoomId;
    private String seatId;
    private LocalDate date;
    private String timeSlot;

    // 获取开始时间
    public LocalDateTime getStartTime() {
        String[] parts = timeSlot.split("-");
        LocalTime start = LocalTime.parse(parts[0]);
        return date.atTime(start);
    }

    // 获取结束时间
    public LocalDateTime getEndTime() {
        String[] parts = timeSlot.split("-");
        LocalTime end = LocalTime.parse(parts[1]);
        return date.atTime(end);
    }
}
