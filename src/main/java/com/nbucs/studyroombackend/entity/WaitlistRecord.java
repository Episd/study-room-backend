package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("waitlistrecord")
public class WaitlistRecord {

    @TableId(value = "waitListRecordID", type = IdType.INPUT)
    private String waitListRecordId;

    @TableField("studentID")
    private Integer studentId;

    @TableField("waitListStartTime")
    private LocalDateTime waitListStartTime;

    @TableField("waitListEndTime")
    private LocalDateTime waitListEndTime;

    @TableField("studyRoomID")
    private Long studyRoomId;

    @TableField("seatID")
    private Long seatId;

    @TableField("seminarRoomID")
    private Long seminarRoomId;

    @TableField("seminarRoomNum")
    private Integer seminarRoomNum;

    @TableField("cancelPermission")
    private Integer cancelPermission;

    @TableField("waitListStatus")
    private Integer waitListStatus;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("priority")
    private Integer priority;
}