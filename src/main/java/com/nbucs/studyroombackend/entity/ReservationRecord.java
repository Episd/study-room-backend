package com.nbucs.studyroombackend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 * 用于存储和管理用户预约自习室、研讨室等相关信息
 */
@Data
public class ReservationRecord {
    private String reservationId;        // 预约记录ID，唯一标识一条预约记录
    private Integer studentId;           // 学生ID，关联到具体学生
    private String studyRoomId;          // 自习室ID，标识预约的自习室
    private String seatId;               // 座位ID，标识预约的具体座位
    private String seminarRoomId;        // 研讨室ID，标识预约的研讨室
    private LocalDateTime reservationStartTime;  // 预约开始时间 yy-mm-dd hh:mm:ss
    private LocalDateTime reservationEndTime;    // 预约结束时间 yy-mm-dd hh:mm:ss
    private Integer reservationRecordStatus;    // 预约状态，0-待审批 1-已通过 2-已开始 3-已结束 4-已取消
    private Integer cancelPermission;    // 取消权限标识，如0表示不可取消，1表示可取消
    private LocalDateTime createlTime;   // 记录创建时间，记录生成的时间戳
}
