package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

/**
 * 自习室类，用于存储和管理自习室的相关信息
 * 使用@Data注解自动生成getter、setter、equals、hashCode和toString方法
 */
@Data
@TableName("studyroom")
public class StudyRoom {
    // 自习室ID，唯一标识一个自习室
    @TableId(value = "studyRoomID", type = IdType.AUTO)
    private Long studyRoomID;
    // 自习室容量，表示自习室可容纳的总人数
    @TableField("studyRoomCapacity")
    private Integer studyRoomCapacity;
    // 自习室位置信息
    @TableField("studyRoomLocation")
    private String studyRoomLocation;
    // 自习室类型，可能用于区分不同类型的自习室
    @TableField("studyRoomType")
    private Integer studyRoomType;
    // 自习室开放时间，使用LocalTime类型表示具体的时间点
    @TableField("studyRoomOpentime")
    private LocalTime studyRoomOpentime;
    // 自习室关闭时间，使用LocalTime类型表示具体的时间点
    @TableField("studyRoomClosetime")
    private LocalTime studyRoomClosetime;
    // 自习室状态，可能表示自习室是否开放、是否维修等状态
    @TableField("status")
    private Integer status;
    // 当前空闲座位数，实时反映自习室的可用座位资源
    @TableField("currentlyIdleSeat")
    private Integer currentlyIdleSeat;

    @TableField("studyRoomName")
    private String studyRoomName;
}
