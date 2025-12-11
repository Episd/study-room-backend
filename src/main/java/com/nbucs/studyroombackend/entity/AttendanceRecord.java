package com.nbucs.studyroombackend.entity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRecord {
    /** 考勤编号，主键 */
    private String attendanceRecordId;
    /** 学号 */
    private int studentId;
    /** 签到时间 */
    private LocalDateTime checkInTime;
    /** 签退时间 */
    private LocalDateTime signOutTime;
    /** 暂离总时长，单位：分钟 */
    private Integer awayDuration;
    private Integer actualStudyDuration;
    private String reservationRecordId;
    private String seatId;
    private String seminarRoomId;
    // 1-正常 2-早退 3-超时 4-异常
    private Integer attendanceStatus;
}
