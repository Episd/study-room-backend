package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

/**
 * 研讨室实体类
 */
@Data
@TableName("seminarroom")
public class SeminarRoom {

    @TableId(value = "seminarRoomID", type = IdType.AUTO)
    private Long seminarRoomID;

    @TableField("seminarRoomLocation")
    private String seminarRoomLocation;

    @TableField("seminarRoomMin")
    private Integer seminarRoomMin;

    @TableField("seminarRoomMax")
    private Integer seminarRoomMax;

    @TableField("seminarRoomStatus")
    private Integer seminarRoomStatus;

    @TableField("currentNum")
    private Integer currentNum;

    @TableField("seminarRoomOpentime")
    private LocalTime seminarRoomOpentime;

    @TableField("seminarRoomName")
    private String seminarRoomName;

    @TableField("seminarRoomClosetime")
    private LocalTime seminarRoomClosetime;
}