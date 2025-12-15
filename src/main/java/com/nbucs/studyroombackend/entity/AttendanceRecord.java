package com.nbucs.studyroombackend.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attendancerecord")
public class AttendanceRecord {
    /** 考勤编号，主键 */
    @TableId(value = "attendanceRecordID", type = IdType.AUTO)
    private Long attendanceRecordId;
    /** 学号 */
    @TableField("studentID")
    private int studentId;
    /** 签到时间 */
    @TableField("checkInTime")
    private LocalDateTime checkInTime;
    /** 签退时间 */
    @TableField("checkOutTime")
    private LocalDateTime checkOutTime;
    /** 暂离总时长，单位：分钟 */
    @TableField("awayDuration")
    private Integer awayDuration;
    @TableField("actualStudyDuration")
    private Integer actualStudyDuration;
    @TableField("reservationRecordID")
    private String reservationRecordId;
    @TableField("studyRoomID")
    private Long studyRoomId;
    @TableField("seatID")
    private Long seatId;
    @TableField("seminarRoomID")
    private Long seminarRoomId;
    // 1-正常 2-早退 3-超时 4-异常
    @TableField("attendanceStatus")
    private Integer attendanceStatus;
    @TableField("awayStartTime")
    private LocalDateTime awayStartTime;
}
