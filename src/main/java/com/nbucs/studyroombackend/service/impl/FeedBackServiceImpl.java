package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbucs.studyroombackend.entity.FeedBack;
import com.nbucs.studyroombackend.mapper.FeedBackMapper;
import com.nbucs.studyroombackend.service.FeedBackService;
import com.nbucs.studyroombackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class FeedBackServiceImpl implements FeedBackService {
    @Autowired
    private FeedBackMapper feedBackMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public FeedBack submitFeedback(FeedBack feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("反馈信息不能为空");
        }

        // 验证必要字段
        if (feedback.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (feedback.getFeedbackType() == null) {
            throw new IllegalArgumentException("反馈类型不能为空");
        }

        if (feedback.getFeedbackContent() == null ||
                feedback.getFeedbackContent().trim().isEmpty()) {
            throw new IllegalArgumentException("反馈内容不能为空");
        }

        // 生成ID（如果未提供）
        if (feedback.getFeedbackID() == null || feedback.getFeedbackID().isEmpty()) {
            feedback.setFeedbackID(generateFeedbackId());
        }

        // 设置默认值
        if (feedback.getProcessStatus() == null) {
            feedback.setProcessStatus(0); // 待处理
        }

        if (feedback.getFeedbackTime() == null) {
            feedback.setFeedbackTime(LocalDateTime.now());
        }

        if (feedback.getPriority() == null) {
            feedback.setPriority(2); // 默认中等优先级
        }

        // 清理内容（去除首尾空格）
        if (feedback.getFeedbackContent() != null) {
            feedback.setFeedbackContent(feedback.getFeedbackContent().trim());
        }

        if (feedback.getContactInfo() != null) {
            feedback.setContactInfo(feedback.getContactInfo().trim());
        }

        // 插入数据库
        int result = feedBackMapper.insert(feedback);
        if (result <= 0) {
            throw new RuntimeException("提交反馈失败");
        }

        return feedback;
    }

    @Override
    public List<FeedBack> getStudentFeedbacks(Integer studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        // 构建查询条件
        QueryWrapper<FeedBack> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentID", studentId)
                .orderByDesc("feedbackTime"); // 按反馈时间倒序排列

        // 执行查询
        List<FeedBack> feedbacks = feedBackMapper.selectList(queryWrapper);

        return feedbacks;
    }

    @Override
    public boolean updateFeedbackStatus(String feedbackId, Integer newStatus) {
        // 参数验证
        if (feedbackId == null || feedbackId.trim().isEmpty()) {
            throw new IllegalArgumentException("反馈ID不能为空");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("新状态不能为空");
        }

        // 验证状态值是否有效（1-待处理，2-处理中，3-已回复，4-已关闭）
        if (newStatus < 1 || newStatus > 4) {
            throw new IllegalArgumentException("无效的状态值，有效值：1-待处理，2-处理中，3-已回复，4-已关闭");
        }

        // 1. 查询反馈记录是否存在
        FeedBack feedback = feedBackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("反馈记录不存在");
        }

        // 2. 检查状态是否可以修改
        // 例如：如果已经是已关闭状态，可能不允许再修改
        if (feedback.getProcessStatus() != null && feedback.getProcessStatus() == 4) {
            throw new RuntimeException("已关闭的反馈不允许修改状态");
        }

        // 3. 创建更新对象
        FeedBack updateFeedback = new FeedBack();
        updateFeedback.setFeedbackID(feedbackId);
        updateFeedback.setProcessStatus(newStatus);

        // 如果是设置为已回复或已关闭状态，更新回复时间
        if (newStatus == 3 || newStatus == 4) {
            if (feedback.getReplyTime() == null) {
                updateFeedback.setReplyTime(LocalDateTime.now());
            }
        }

        // 4. 执行更新
        int result = feedBackMapper.updateById(updateFeedback);

        // 5. 如果更新成功且是状态3（已回复）或状态4（已关闭），发送通知
        if (result > 0 && (newStatus == 3 || newStatus == 4)) {
            try {
                // 查询更新后的反馈记录
                FeedBack updatedFeedback = feedBackMapper.selectById(feedbackId);
                if (updatedFeedback != null) {
                    // 发送反馈处理通知
                    boolean notificationSent = notificationService.sendFeedbackProcessedNotification(updatedFeedback);

                    if (notificationSent) {
                        System.out.println("反馈状态更新成功，通知发送成功，反馈ID: " + feedbackId);
                    } else {
                        System.err.println("反馈状态更新成功，但通知发送失败，反馈ID: " + feedbackId);
                    }
                }
            } catch (Exception e) {
                System.err.println("发送通知时出现异常，但反馈状态已更新。反馈ID: " + feedbackId + ", 异常: " + e.getMessage());
                // 通知发送失败不影响主操作，记录日志但不抛出异常
            }
        }

        return result > 0;
    }

    /**
     * 生成反馈ID
     * @return 反馈ID
     */
    private String generateFeedbackId() {
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天已生成的反馈数量
        QueryWrapper<FeedBack> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("feedbackID", "FB" + datePart)  // 查找以FB+日期开头的记录
                .orderByDesc("feedbackID")
                .last("LIMIT 1");

        FeedBack lastFeedback = feedBackMapper.selectOne(queryWrapper);

        int sequence = 1; // 默认从0001开始

        if (lastFeedback != null && lastFeedback.getFeedbackID() != null) {
            String lastId = lastFeedback.getFeedbackID();
            if (lastId.startsWith("FB" + datePart) && lastId.length() >= 12) {
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

        return "FB" + datePart + sequencePart;
    }
}
