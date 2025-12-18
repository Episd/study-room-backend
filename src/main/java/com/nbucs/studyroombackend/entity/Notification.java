package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(value = "notificationID", type = IdType.INPUT)
    private String notificationID;

    @TableField("adminID")
    private Integer adminID;

    @TableField("studentID")
    private Integer studentID;

    @TableField("sendTime")
    private LocalDateTime sendTime;

    @TableField("notificationStatus")
    private Integer notificationStatus;  // 通知状态，如：0-未发送，1-已发送，2-已读，3-已过期

    @TableField("notificationType")
    private Integer notificationType;    // 通知类型：1-违规通知，2-反馈通知，3-提醒通知，4-系统通知

    @TableField("notificationContent")
    private String notificationContent;

    @TableField("title")
    private String title;

    @TableField("relatedRecordID")
    private String relatedRecordID;     // 关联记录ID，如预约ID、反馈ID等

    @TableField("readTime")
    private LocalDateTime readTime;

    @TableField("expireTime")
    private LocalDateTime expireTime;
}