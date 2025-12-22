package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.FeedBack;
import com.nbucs.studyroombackend.entity.Notification;
import com.nbucs.studyroombackend.entity.ViolationRecord;

import java.util.List;

public interface NotificationService {
    /**
     * 发送反馈提交确认通知
     * @param feedback 已提交的反馈记录
     * @return 创建的通知记录
     */
    Notification sendFeedbackSubmittedNotification(FeedBack feedback);

    /**
     * 发送反馈处理通知
     * @param feedback 已处理的反馈记录
     * @return 返回该记录
     */
    Notification sendFeedbackProcessedNotification(FeedBack feedback);

    /**
     * 发送违规通知
     * @param violationRecord 违规记录
     * @return 创建的通知记录
     */
    Notification sendViolationNotification(ViolationRecord violationRecord);

    /**
     * 发送申诉驳回通知
     * @param violationRecord 违规记录（需包含申诉信息）
     * @return 创建的通知记录
     */
    Notification sendAppealRejectedNotification(ViolationRecord violationRecord);

    /**
     * 发送申诉通过通知
     * @param violationRecord 违规记录（需包含申诉信息）
     * @return 创建的通知记录
     */
    Notification sendAppealApprovedNotification(ViolationRecord violationRecord);

    /**
     * 多条件查询通知记录
     * @param studentId 学生ID（可选）
     * @param notificationType 通知类型（可选）
     * @param notificationStatus 通知状态（可选）
     * @param adminId 管理员ID（可选）
     * @return 通知记录列表
     */
    List<Notification> queryNotifications(Integer studentId, Integer notificationType,
                                          Integer notificationStatus, Integer adminId);

    /**
     * 标记通知为已查看
     * @param notificationId 通知ID
     * @return 更新是否成功
     */
    boolean markNotificationAsRead(String notificationId);

    /**
     * 批量标记通知为已查看
     * @return 成功更新的数量
     */
    int markStudentNotificationsAsRead(Integer studentId);

    /**
     * 根据通知ID删除通知
     * @param notificationId 通知ID
     * @return 删除是否成功
     */
    boolean deleteNotificationById(String notificationId);

    /**
     * 根据学生ID删除通知
     * @param studentId 学生ID
     * @return 成功删除的数量
     */
    int deleteNotificationsByStudentId(Integer studentId);
}
