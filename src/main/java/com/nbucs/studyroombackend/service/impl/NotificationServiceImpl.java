package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nbucs.studyroombackend.entity.FeedBack;
import com.nbucs.studyroombackend.entity.Notification;
import com.nbucs.studyroombackend.mapper.FeedBackMapper;
import com.nbucs.studyroombackend.mapper.NotificationMapper;
import com.nbucs.studyroombackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NavigableMap;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private FeedBackMapper feedBackMapper;

    @Override
    public boolean sendFeedbackProcessedNotification(FeedBack feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不能为空");
        }

        // 验证必要字段
        if (feedback.getFeedbackID() == null) {
            throw new IllegalArgumentException("反馈ID不能为空");
        }

        if (feedback.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (feedback.getProcessAdminID() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        if (feedback.getFeedbackContent() == null) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }

        try {
            // 1. 创建通知记录
            Notification notification = new Notification();

            // 2. 生成通知ID（格式：NT202512180001）
            notification.setNotificationID(generateNotificationId());

            // 3. 设置管理员ID（处理反馈的管理员）
            notification.setAdminID(feedback.getProcessAdminID());

            // 4. 设置学生ID（提交反馈的学生）
            notification.setStudentID(feedback.getStudentID());

            // 5. 设置发送时间（当前时间）
            notification.setSendTime(LocalDateTime.now());

            // 6. 设置通知状态：1-未查看（默认）
            notification.setNotificationStatus(1);

            // 7. 设置通知类型：2-反馈通知
            notification.setNotificationType(2);

            // 8. 设置标题
            notification.setTitle("反馈通知");

            // 9. 设置关联记录ID（反馈ID）
            notification.setRelatedRecordID(feedback.getFeedbackID());

            // 10. readTime和expireTime暂时为空
            notification.setReadTime(null);
            notification.setExpireTime(null);

            // 11. 构建通知内容
            String notificationContent = buildFeedbackNotificationContent(feedback);
            notification.setNotificationContent(notificationContent);

            // 12. 保存通知记录
            int result = notificationMapper.insert(notification);

            return result > 0;

        } catch (Exception e) {
            System.err.println("发送反馈处理通知失败: " + e.getMessage());
            throw new RuntimeException("发送通知失败: " + e.getMessage(), e);
        }
    }
    /**
     * 构建反馈通知内容
     * @param feedback 反馈记录
     * @return 通知内容
     */
    private String buildFeedbackNotificationContent(FeedBack feedback) {
        StringBuilder content = new StringBuilder();
        content.append("您提交的反馈已处理！");

        // 添加反馈信息
        if (feedback.getFeedbackContent() != null) {
            String shortContent = feedback.getFeedbackContent();
            // 如果内容过长，截取前50个字符
            if (shortContent.length() > 50) {
                shortContent = shortContent.substring(0, 50) + "...";
            }
            content.append("反馈信息：").append(shortContent);
        }

        // 添加处理结果
        if (feedback.getReplyContent() != null) {
            String shortReply = feedback.getReplyContent();
            // 如果回复内容过长，截取前50个字符
            if (shortReply.length() > 50) {
                shortReply = shortReply.substring(0, 50) + "...";
            }
            content.append("。处理结果：").append(shortReply);
        } else {
            content.append("。处理结果：已处理完成");
        }

        // 添加处理时间
        if (feedback.getReplyTime() != null) {
            content.append("（处理时间：").append(feedback.getReplyTime().toLocalDate()).append("）");
        }

        return content.toString();
    }

    public String generateNotificationId() {
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天已生成的通知数量
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("notificationID", "NT" + datePart)  // 查找以NT+日期开头的记录
                .orderByDesc("notificationID")
                .last("LIMIT 1");

        Notification lastNotification = notificationMapper.selectOne(queryWrapper);

        int sequence = 1; // 默认从0001开始

        if (lastNotification != null && lastNotification.getNotificationID() != null) {
            String lastId = lastNotification.getNotificationID();
            if (lastId.startsWith("NT" + datePart) && lastId.length() >= 12) {
                try {
                    String seqStr = lastId.substring(10); // 取最后4位
                    sequence = Integer.parseInt(seqStr) + 1;
                    // 确保序号在0001-9999范围内
                    if (sequence > 9999) {
                        sequence = 1; // 如果超过9999，重置为0001
                    }
                } catch (NumberFormatException e) {
                    // 如果解析失败，从1开始
                    sequence = 1;
                }
            }
        }

        // 生成序号部分，格式化为4位数字
        String sequencePart = String.format("%04d", sequence);

        return "NT" + datePart + sequencePart;
    }

    @Override
    public boolean markNotificationAsRead(String notificationId) {
        // 参数验证
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("通知ID不能为空");
        }

        try {
            // 1. 查询通知记录是否存在
            Notification notification = notificationMapper.selectById(notificationId);
            if (notification == null) {
                throw new RuntimeException("通知记录不存在，ID：" + notificationId);
            }

            // 2. 检查通知是否已查看（避免重复更新）
            if (notification.getNotificationStatus() != null &&
                    notification.getNotificationStatus() == 2) {
                // 已经是已查看状态，但需要确保readTime有值
                if (notification.getReadTime() == null) {
                    Notification updateTime = new Notification();
                    updateTime.setNotificationID(notificationId);
                    updateTime.setReadTime(LocalDateTime.now());
                    notificationMapper.updateById(updateTime);
                }
                return true; // 已经是已查看状态，直接返回成功
            }

            // 3. 检查通知是否有效（非过期、非已发送状态才允许标记为已读）
            if (notification.getNotificationStatus() != null &&
                    notification.getNotificationStatus() != 1) {
                throw new RuntimeException("只有未查看的通知才能标记为已读");
            }

            // 4. 检查通知是否已过期
            if (notification.getExpireTime() != null &&
                    notification.getExpireTime().isBefore(LocalDateTime.now())) {
                // 已过期的通知自动标记为已过期状态
                Notification updateExpired = new Notification();
                updateExpired.setNotificationID(notificationId);
                updateExpired.setNotificationStatus(3); // 3-已过期
                notificationMapper.updateById(updateExpired);
                throw new RuntimeException("该通知已过期，无法标记为已读");
            }

            // 5. 创建更新对象
            Notification updateNotification = new Notification();
            updateNotification.setNotificationID(notificationId);
            updateNotification.setNotificationStatus(2); // 2-已查看
            updateNotification.setReadTime(LocalDateTime.now()); // 设置查看时间

            // 6. 执行更新
            int result = notificationMapper.updateById(updateNotification);

            if (result > 0) {
                System.out.println("通知标记为已查看成功，通知ID：" + notificationId);
                return true;
            } else {
                System.err.println("通知标记为已查看失败，通知ID：" + notificationId);
                return false;
            }

        } catch (RuntimeException e) {
            throw e; // 重新抛出业务异常
        } catch (Exception e) {
            System.err.println("标记通知为已查看失败，通知ID：" + notificationId + "，错误：" + e.getMessage());
            throw new RuntimeException("标记通知为已查看失败：" + e.getMessage(), e);
        }
    }

    @Override
    public int markStudentNotificationsAsRead(Integer studentId) {
        // 参数验证
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        try {
            LocalDateTime now = LocalDateTime.now();

            // 方法1：使用UpdateWrapper直接批量更新（推荐，效率最高）
            UpdateWrapper<Notification> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("studentID", studentId)
                    .eq("notificationStatus", 1) // 只更新未查看状态的通知
                    .set("notificationStatus", 2) // 设置为已查看
                    .set("readTime", now);

            int updatedCount = notificationMapper.update(null, updateWrapper);

            System.out.println("批量标记学生通知为已查看，学生ID：" + studentId +
                    "，成功标记数量：" + updatedCount);

            return updatedCount;

        } catch (Exception e) {
            System.err.println("批量标记学生通知为已查看失败，学生ID：" + studentId +
                    "，错误：" + e.getMessage());
            throw new RuntimeException("批量标记通知为已查看失败：" + e.getMessage(), e);
        }
    }
}
