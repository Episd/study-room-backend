package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.FeedBack;

import java.util.List;

public interface FeedBackService {
    /**
     * 提交反馈（使用实体对象）
     * @param feedback 反馈实体
     * @return 反馈记录
     */
    FeedBack submitFeedback(FeedBack feedback);

    /**
     * 获取学生的所有反馈记录
     * @param studentId 学生ID
     * @return 反馈记录列表
     */
    List<FeedBack> getStudentFeedbacks(Integer studentId);

    /**
     * 更新反馈状态
     * @param feedbackId 反馈ID
     * @param newStatus 新状态
     * @return 更新是否成功
     */
    boolean updateFeedbackStatus(String feedbackId, Integer newStatus);
}
