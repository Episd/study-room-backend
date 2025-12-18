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

    @TableId(value = "reservationRecordID", type = IdType.INPUT)// 自动生成 UUID
    private String reservationRecordID;

    @TableField("studentID")
    private Integer studentID;

    @TableField("studyRoomID")
    private Long studyRoomID;

    @TableField("seatID")
    private Long seatID;

    @TableField("seminarRoomID")
    private Long seminarRoomID;

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