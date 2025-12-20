package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nbucs.studyroombackend.entity.FeedBack;
import com.nbucs.studyroombackend.entity.Notification;
import com.nbucs.studyroombackend.entity.ViolationRecord;
import com.nbucs.studyroombackend.mapper.FeedBackMapper;
import com.nbucs.studyroombackend.mapper.NotificationMapper;
import com.nbucs.studyroombackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private FeedBackMapper feedBackMapper;

    @Override
    @Transactional
    public Notification sendFeedbackSubmittedNotification(FeedBack feedback) {
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

        try {
            // 1. 创建通知记录
            Notification notification = new Notification();

            // 2. 生成通知ID
            notification.setNotificationID(generateNotificationId());

            // 3. 设置管理员ID（可以为空，因为是系统自动通知）
            notification.setAdminID(feedback.getProcessAdminID()); // 可能为null

            // 4. 设置学生ID（提交反馈的学生）
            notification.setStudentID(feedback.getStudentID());

            // 5. 设置发送时间（当前时间）
            notification.setSendTime(LocalDateTime.now());

            // 6. 设置通知状态：1-未查看（默认）
            notification.setNotificationStatus(1);

            // 7. 设置通知类型：2-反馈通知
            notification.setNotificationType(2);

            // 8. 设置标题
            notification.setTitle("反馈提交成功");

            // 9. 设置关联记录ID（反馈ID）
            notification.setRelatedRecordID(feedback.getFeedbackID());

            // 10. readTime和expireTime为空
            notification.setReadTime(null);
            notification.setExpireTime(null);

            // 11. 构建通知内容
            String notificationContent = buildFeedbackSubmittedContent(feedback);
            notification.setNotificationContent(notificationContent);

            // 12. 保存通知记录
            int result = notificationMapper.insert(notification);

            if (result > 0) {
                return notification; // 返回创建的通知对象
            } else {
                throw new RuntimeException("发送反馈提交通知失败：保存通知记录失败");
            }

        } catch (Exception e) {
            System.err.println("发送反馈提交通知失败: " + e.getMessage());
            throw new RuntimeException("发送反馈提交通知失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建反馈提交通知内容
     */
    private String buildFeedbackSubmittedContent(FeedBack feedback) {
        StringBuilder content = new StringBuilder();

        // 格式化提交时间
        String feedbackTimeStr = feedback.getFeedbackTime() != null ?
                feedback.getFeedbackTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                "刚刚";

        content.append("感谢您的反馈！您的反馈已成功提交（提交时间：")
                .append(feedbackTimeStr)
                .append("）。");

        return content.toString();
    }

    @Override
    public Notification sendFeedbackProcessedNotification(FeedBack feedback) {
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

            if (result > 0) {
                return notification; // 返回创建的通知对象
            } else {
                throw new RuntimeException("发送通知失败：保存通知记录失败");
            }

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

    @Override
    public Notification sendViolationNotification(ViolationRecord violationRecord) {
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        // 验证必要字段
        if (violationRecord.getViolationRecordID() == null) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (violationRecord.getDetails() == null) {
            throw new IllegalArgumentException("违规详情不能为空");
        }

        if (violationRecord.getViolationTime() == null) {
            throw new IllegalArgumentException("违规时间不能为空");
        }

        if (violationRecord.getDeductPoints() == null) {
            throw new IllegalArgumentException("扣除积分数不能为空");
        }

        try {
            // 1. 创建通知记录
            Notification notification = new Notification();

            // 2. 生成通知ID
            notification.setNotificationID(generateNotificationId());

            // 3. 设置管理员ID（处理违规的管理员）
            notification.setAdminID(violationRecord.getAdminID());

            // 4. 设置学生ID（触发违规的学生）
            notification.setStudentID(violationRecord.getStudentID());

            // 5. 设置发送时间（当前时间）
            notification.setSendTime(LocalDateTime.now());

            // 6. 设置通知状态：1-未查看（默认）
            notification.setNotificationStatus(1);

            // 7. 设置通知类型：1-违规通知
            notification.setNotificationType(1);

            // 8. 设置标题
            notification.setTitle("违规通知");

            // 9. 设置关联记录ID（违规记录ID）
            notification.setRelatedRecordID(violationRecord.getViolationRecordID());

            // 10. readTime和expireTime为空
            notification.setReadTime(null);
            notification.setExpireTime(null);

            // 11. 构建通知内容
            String notificationContent = buildViolationNotificationContent(violationRecord);
            notification.setNotificationContent(notificationContent);

            // 12. 保存通知记录
            int result = notificationMapper.insert(notification);

            if (result > 0) {
                return notification; // 返回创建的通知对象
            } else {
                throw new RuntimeException("发送违规通知失败：保存通知记录失败");
            }

        } catch (Exception e) {
            System.err.println("发送违规通知失败: " + e.getMessage());
            throw new RuntimeException("发送违规通知失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建违规通知内容
     */
    private String buildViolationNotificationContent(ViolationRecord violationRecord) {
        StringBuilder content = new StringBuilder();

        // 格式化违规时间
        String violationTimeStr = violationRecord.getViolationTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        content.append("您于")
                .append(violationTimeStr)
                .append("，")
                .append(violationRecord.getDetails())
                .append("，违反自习室规则，扣除")
                .append(violationRecord.getDeductPoints())
                .append("积分。如有疑问，可于通知后三天内进行违规申诉！");

        return content.toString();
    }

    @Override
    @Transactional
    public Notification sendAppealRejectedNotification(ViolationRecord violationRecord) {
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        // 验证必要字段
        if (violationRecord.getViolationRecordID() == null) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (violationRecord.getAdminID() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        if (violationRecord.getAppealTime() == null) {
            throw new IllegalArgumentException("申诉时间为空，无法生成申诉驳回通知");
        }

        try {
            // 1. 创建通知记录
            Notification notification = new Notification();

            // 2. 生成通知ID
            notification.setNotificationID(generateNotificationId());

            // 3. 设置管理员ID（处理申诉的管理员）
            notification.setAdminID(violationRecord.getAdminID());

            // 4. 设置学生ID（提交申诉的学生）
            notification.setStudentID(violationRecord.getStudentID());

            // 5. 设置发送时间（当前时间）
            notification.setSendTime(LocalDateTime.now());

            // 6. 设置通知状态：1-未查看（默认）
            notification.setNotificationStatus(1);

            // 7. 设置通知类型：2-反馈通知（申诉驳回属于反馈类型）
            notification.setNotificationType(2);

            // 8. 设置标题
            notification.setTitle("违规申诉驳回");

            // 9. 设置关联记录ID（违规记录ID）
            notification.setRelatedRecordID(violationRecord.getViolationRecordID());

            // 10. readTime和expireTime为空
            notification.setReadTime(null);
            notification.setExpireTime(null);

            // 11. 构建通知内容
            String notificationContent = buildAppealRejectedContent(violationRecord);
            notification.setNotificationContent(notificationContent);

            // 12. 保存通知记录
            int result = notificationMapper.insert(notification);

            if (result > 0) {
                return notification; // 返回创建的通知对象
            } else {
                throw new RuntimeException("发送申诉驳回通知失败：保存通知记录失败");
            }

        } catch (Exception e) {
            System.err.println("发送申诉驳回通知失败: " + e.getMessage());
            throw new RuntimeException("发送申诉驳回通知失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建申诉驳回通知内容
     */
    private String buildAppealRejectedContent(ViolationRecord violationRecord) {
        StringBuilder content = new StringBuilder();

        // 格式化申诉时间
        String appealTimeStr = violationRecord.getAppealTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        content.append("您于")
                .append(appealTimeStr)
                .append("提交的违规申诉，经管理员审核，不予通过。");

        // 如果有处理意见，可以添加到通知中
        if (violationRecord.getProcessTime() != null) {
            content.append("（处理时间：")
                    .append(violationRecord.getProcessTime().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .append("）");
        }

        return content.toString();
    }

    @Override
    @Transactional
    public Notification sendAppealApprovedNotification(ViolationRecord violationRecord) {
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        // 验证必要字段
        if (violationRecord.getViolationRecordID() == null) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (violationRecord.getAdminID() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        if (violationRecord.getAppealTime() == null) {
            throw new IllegalArgumentException("申诉时间为空，无法生成申诉通过通知");
        }

        if (violationRecord.getDeductPoints() == null) {
            throw new IllegalArgumentException("扣除积分数为空，无法生成申诉通过通知");
        }

        try {
            // 1. 创建通知记录
            Notification notification = new Notification();

            // 2. 生成通知ID
            notification.setNotificationID(generateNotificationId());

            // 3. 设置管理员ID（处理申诉的管理员）
            notification.setAdminID(violationRecord.getAdminID());

            // 4. 设置学生ID（提交申诉的学生）
            notification.setStudentID(violationRecord.getStudentID());

            // 5. 设置发送时间（当前时间）
            notification.setSendTime(LocalDateTime.now());

            // 6. 设置通知状态：1-未查看（默认）
            notification.setNotificationStatus(1);

            // 7. 设置通知类型：2-反馈通知（申诉通过属于反馈类型）
            notification.setNotificationType(2);

            // 8. 设置标题
            notification.setTitle("违规申诉通过");

            // 9. 设置关联记录ID（违规记录ID）
            notification.setRelatedRecordID(violationRecord.getViolationRecordID());

            // 10. readTime和expireTime为空
            notification.setReadTime(null);
            notification.setExpireTime(null);

            // 11. 构建通知内容
            String notificationContent = buildAppealApprovedContent(violationRecord);
            notification.setNotificationContent(notificationContent);

            // 12. 保存通知记录
            int result = notificationMapper.insert(notification);

            if (result > 0) {
                return notification; // 返回创建的通知对象
            } else {
                throw new RuntimeException("发送申诉通过通知失败：保存通知记录失败");
            }

        } catch (Exception e) {
            System.err.println("发送申诉通过通知失败: " + e.getMessage());
            throw new RuntimeException("发送申诉通过通知失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建申诉通过通知内容
     */
    private String buildAppealApprovedContent(ViolationRecord violationRecord) {
        StringBuilder content = new StringBuilder();

        // 格式化申诉时间
        String appealTimeStr = violationRecord.getAppealTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        content.append("您于")
                .append(appealTimeStr)
                .append("提交的违规申诉，经管理员审核，已通过，已恢复扣除的")
                .append(violationRecord.getDeductPoints())
                .append("信用积分。");

        // 如果有处理时间，可以添加到通知中
        if (violationRecord.getProcessTime() != null) {
            content.append("（处理时间：")
                    .append(violationRecord.getProcessTime().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .append("）");
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
    public List<Notification> queryNotifications(Integer studentId, Integer notificationType,
                                                 Integer notificationStatus, Integer adminId) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();

        // 按发送时间倒序排列
        queryWrapper.orderByDesc("sendTime");

        // 条件判断：如果不为null，则添加查询条件
        if (studentId != null) {
            queryWrapper.eq("studentID", studentId);
        }

        if (notificationType != null) {
            queryWrapper.eq("notificationType", notificationType);
        }

        if (notificationStatus != null) {
            queryWrapper.eq("notificationStatus", notificationStatus);
        }

        if (adminId != null) {
            queryWrapper.eq("adminID", adminId);
        }

        return notificationMapper.selectList(queryWrapper);
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

    @Override
    @Transactional
    public boolean deleteNotificationById(String notificationId) {
        // 参数验证
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("通知ID不能为空");
        }

        try {
            // 执行删除
            int result = notificationMapper.deleteById(notificationId);

            if (result > 0) {
                System.out.println("删除通知成功，通知ID：" + notificationId);
                return true;
            } else {
                System.out.println("通知不存在或删除失败，通知ID：" + notificationId);
                return false;
            }

        } catch (Exception e) {
            System.err.println("删除通知失败，通知ID：" + notificationId + "，错误：" + e.getMessage());
            throw new RuntimeException("删除通知失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int deleteNotificationsByStudentId(Integer studentId) {
        // 参数验证
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        try {
            // 创建删除条件
            QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("studentID", studentId);

            // 执行删除
            int deletedCount = notificationMapper.delete(queryWrapper);

            System.out.println("删除学生[" + studentId + "]的所有通知，成功删除" + deletedCount + "条");

            return deletedCount;

        } catch (Exception e) {
            System.err.println("删除学生通知失败，学生ID：" + studentId + "，错误：" + e.getMessage());
            throw new RuntimeException("删除学生通知失败：" + e.getMessage(), e);
        }
    }
}
