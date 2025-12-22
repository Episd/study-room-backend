package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("violationrecord")
public class ViolationRecord {

    @TableId(value = "violationRecordID", type = IdType.INPUT)
    private String violationRecordID;

    @TableField("studentID")
    private Integer studentID;

    @TableField("adminID")
    private Integer adminID;

    @TableField("violationType")
    private Integer violationType;

    @TableField("details")
    private String details;

    @TableField("deductPoints")
    private Integer deductPoints;

    @TableField("violationTime")
    private LocalDateTime violationTime;

    @TableField("status")
    private Integer status;

    @TableField("reservationRecordID")
    private String reservationRecordID;

    @TableField("attendanceRecordID")
    private Long attendanceRecordID;

    @TableField("appealReason")
    private String appealReason;

    @TableField("appealTime")
    private LocalDateTime appealTime;

    @TableField("processTime")
    private LocalDateTime processTime;

    @TableField("createTime")
    private LocalDateTime createTime;
}