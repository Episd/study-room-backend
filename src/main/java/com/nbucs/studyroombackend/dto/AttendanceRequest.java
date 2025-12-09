package com.nbucs.studyroombackend.dto;

import java.time.LocalDateTime;

/**
 * 学生发起考勤操作时的请求参数
 * 对应文档中 StudentAttendance / 考勤管理接口的“check”结构
 */

public class AttendanceRequest {
    /** 学号 */
    private int studentId;

    /** 自习室编号，可为空 */
    private String studyRoomId;

    /** 座位编号，可为空 */
    private String seatId;

    /** 研讨室编号，可为空 */
    private String seminarRoomId;

    /** 本次操作时间（签到/签退/暂离） */
    private LocalDateTime operateTime;

    /** 本次暂离时长（leaveTemporarily 时使用，单位：分钟） */
    private Integer durationMinutes;

    // getter / setter 省略也可以自己生成

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudyRoomId() {
        return studyRoomId;
    }

    public void setStudyRoomId(String studyRoomId) {
        this.studyRoomId = studyRoomId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getSeminarRoomId() {
        return seminarRoomId;
    }

    public void setSeminarRoomId(String seminarRoomId) {
        this.seminarRoomId = seminarRoomId;
    }

    public LocalDateTime getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(LocalDateTime operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
