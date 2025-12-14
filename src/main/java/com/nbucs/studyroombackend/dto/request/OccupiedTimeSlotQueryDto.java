package com.nbucs.studyroombackend.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OccupiedTimeSlotQueryDto {
    // 自习室ID（和研讨室ID二选一）
    private String studyRoomId;

    // 座位ID（如果查询座位时使用）
    private String seatId;

    // 研讨室ID（和自习室ID二选一）
    private String seminarRoomId;

    // 预约日期（必填）
    private LocalDate queryDate;
}