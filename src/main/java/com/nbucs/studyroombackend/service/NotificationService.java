package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.FeedBack;

import java.util.List;

public interface NotificationService {
    /**
     * 发送反馈处理通知
     * @param feedback 已处理的反馈记录
     * @return 发送是否成功
     */
    boolean sendFeedbackProcessedNotification(FeedBack feedback);

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
}
