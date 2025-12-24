package com.nbucs.studyroombackend.service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbucs.studyroombackend.entity.FeedBack;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedBackService {
    /**
     * 提交反馈（使用实体对象）
     * @param feedback 反馈实体
     * @return 反馈记录
     */
    FeedBack submitFeedback(FeedBack feedback);

    /**
     * 更新反馈记录内容，处理反馈
     * @param feedback 包含更新内容的反馈对象（必须包含feedbackID）
     * @return 更新是否成功
     */
    boolean updateFeedback(FeedBack feedback);

    /**
     * 更新反馈状态
     * @param feedbackId 反馈ID
     * @param newStatus 新状态
     * @return 更新是否成功
     */
    boolean updateFeedbackStatus(String feedbackId, Integer newStatus);

    /**
     * 获取学生的所有反馈记录
     * @param studentId 学生ID
     * @return 反馈记录列表
     */
    List<FeedBack> getStudentFeedbacks(Integer studentId);

    /**
     * 获取管理员负责的所有反馈记录
     * @param AdminId 管理员ID
     * @return 反馈记录列表
     */
    List<FeedBack> getAdminFeedbacks(Integer AdminId);

    /**
     * 根据时间范围查询反馈记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 反馈记录列表
     */
    List<FeedBack> getTimeFeedbacks(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 状态查询
     * @param processStatus  状态
     * @return  结果
     */
    List<FeedBack> queryFeedBacks(Integer processStatus);

    /** 支持筛选 + 分页：获取学生反馈 */
    IPage<FeedBack> getStudentFeedbackPage(Integer studentId,
                                           Integer processStatus,
                                           Integer feedbackType,
                                           String keyword,
                                           Integer page,
                                           Integer size);

}
