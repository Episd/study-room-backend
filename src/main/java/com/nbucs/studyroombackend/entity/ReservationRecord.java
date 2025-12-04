package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reservationrecord")
public class ReservationRecord {

    @TableId(value = "reservationRecordID", type = IdType.INPUT) // 修正这里
    private String reservationId;

    @TableField("studentID")
    private Integer studentId;

    @TableField("studyRoomID")
    private String studyRoomId;

    @TableField("seatID")
    private String seatId;

    @TableField("seminarRoomID")
    private String seminarRoomId;

    @TableField("seminarRoomNum")
    private Integer seminarRoomNum;

    @TableField("reservationStartTime")
    private LocalDateTime reservationStartTime;

    @TableField("reservationEndTime")
    private LocalDateTime reservationEndTime;

    @TableField("reservationRecordStatus")
    private Integer reservationRecordStatus;

    @TableField("cancelPermission")
    private Integer cancelPermission;

    @TableField("createTime")
    private LocalDateTime createTime;
}