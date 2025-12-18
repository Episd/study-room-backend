package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class FeedBack {
    @TableId(value = "feedbackID",type = IdType.INPUT)
    private String feedbackID;

    @TableField("studentID")
    private Integer studentID;

    @TableField("processAdminID")
    private Integer processAdminID;

    @TableField("feedbackType")
    private Integer feedbackType;  // 或者String类型，根据实际设计

    @TableField("feedbackContent")
    private String feedbackContent;

    @TableField("processStatus")
    private Integer processStatus;  // 处理状态，例如：0-未处理，1-处理中，2-已处理

    @TableField("feedbackTime")
    private LocalDateTime feedbackTime;

    @TableField("replyContent")
    private String replyContent;

    @TableField("replyTime")
    private LocalDateTime replyTime;

    @TableField("contactInfo")
    private String contactInfo;

    @TableField("relatedResourceID")
    private String relatedResourceID;  // 或者根据实际情况用Long/Integer

    @TableField("priority")
    private Integer priority;  // 优先级，例如：1-低，2-中，3-高
}
