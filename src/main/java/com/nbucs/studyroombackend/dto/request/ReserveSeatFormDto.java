package com.nbucs.studyroombackend.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReserveSeatFormDto {
    private Integer studentId;
    private Long studyRoomId;
    private Long seatId;

    // ===== 旧字段：普通座位用 =====
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String timeSlot;

    // ===== 新字段：考研按周/跨天用 =====
    // 兼容前端传 startTime/endTime
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonAlias({"reservationStartTime"}) // 如果你前端曾经传过这个，也兼容
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonAlias({"reservationEndTime"})   // 同上
    private LocalDateTime endTime;

    // 获取开始时间（优先使用 startTime；否则用 date+timeSlot 计算）
    public LocalDateTime getStartTime() {
        if (startTime != null) return startTime;

        if (date == null) {
            throw new IllegalArgumentException("缺少 date（普通座位需要 date + timeSlot，或直接传 startTime/endTime）");
        }
        if (timeSlot == null || timeSlot.isBlank()) {
            throw new IllegalArgumentException("缺少 timeSlot（普通座位需要 date + timeSlot，或直接传 startTime/endTime）");
        }

        String[] parts = timeSlot.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("timeSlot 格式错误，应为 HH:mm-HH:mm，例如 08:00-10:00");
        }
        LocalTime start = LocalTime.parse(parts[0].trim());
        return date.atTime(start);
    }

    // 获取结束时间（优先使用 endTime；否则用 date+timeSlot 计算）
    public LocalDateTime getEndTime() {
        if (endTime != null) return endTime;

        if (date == null) {
            throw new IllegalArgumentException("缺少 date（普通座位需要 date + timeSlot，或直接传 startTime/endTime）");
        }
        if (timeSlot == null || timeSlot.isBlank()) {
            throw new IllegalArgumentException("缺少 timeSlot（普通座位需要 date + timeSlot，或直接传 startTime/endTime）");
        }

        String[] parts = timeSlot.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("timeSlot 格式错误，应为 HH:mm-HH:mm，例如 08:00-10:00");
        }
        LocalTime end = LocalTime.parse(parts[1].trim());
        return date.atTime(end);
    }
}
