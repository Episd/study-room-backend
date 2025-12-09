package com.nbucs.studyroombackend.entity;
import java.time.LocalDateTime;

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

    // ===== getter / setter =====

    public String getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(String attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(LocalDateTime signOutTime) {
        this.signOutTime = signOutTime;
    }

    public Integer getAwayDuration() {
        return awayDuration;
    }

    public void setAwayDuration(Integer awayDuration) {
        this.awayDuration = awayDuration;
    }
}
