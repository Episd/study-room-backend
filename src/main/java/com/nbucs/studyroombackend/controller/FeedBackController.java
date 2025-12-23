package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.FeedBack;
import com.nbucs.studyroombackend.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedBackController {
    @Autowired
    private FeedBackService feedbackService;

    /**
     * 提交反馈
     * POST /api/feedback/submit
     */
    @PostMapping("/submit")
    public Response<?> submitFeedback(@RequestBody FeedBack feedback) {
        try {
            if (feedback == null) {
                return Response.error(400, "反馈信息不能为空");
            }

            System.out.println("提交反馈请求：学生ID=" + feedback.getStudentID() +
                    ", 反馈类型=" + feedback.getFeedbackType());

            FeedBack savedFeedback = feedbackService.submitFeedback(feedback);

            Map<String, Object> result = new HashMap<>();
            result.put("feedbackId", savedFeedback.getFeedbackID());
            result.put("submitTime", savedFeedback.getFeedbackTime());
            result.put("message", "反馈提交成功");

            return Response.success("反馈提交成功", result);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("提交反馈异常: " + e.getMessage());
            return Response.error(500, "提交失败，请稍后重试");
        }
    }

    /**
     * 更新反馈记录内容
     * PUT /api/feedback/update
     */
    @PutMapping("/update")
    public Response<?> updateFeedback(@RequestBody FeedBack feedback) {
        try {
            if (feedback == null) {
                return Response.error(400, "反馈信息不能为空");
            }

            if (feedback.getFeedbackID() == null || feedback.getFeedbackID().trim().isEmpty()) {
                return Response.error(400, "反馈ID不能为空");
            }

            System.out.println("更新反馈请求：反馈ID=" + feedback.getFeedbackID());

            boolean success = feedbackService.updateFeedback(feedback);

            if (success) {
                return Response.success("反馈更新成功", null);
            } else {
                return Response.error(304, "反馈更新失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("更新反馈异常: " + e.getMessage());
            return Response.error(500, "更新失败，请稍后重试");
        }
    }

    /**
     * 更新反馈状态
     * PUT /api/feedback/status
     */
    @PutMapping("/status")
    public Response<?> updateFeedbackStatus(
            @RequestParam String feedbackId,
            @RequestParam Integer newStatus) {

        try {
            if (feedbackId == null || feedbackId.trim().isEmpty()) {
                return Response.error(400, "反馈ID不能为空");
            }

            if (newStatus == null) {
                return Response.error(400, "新状态不能为空");
            }

            System.out.println("更新反馈状态请求：反馈ID=" + feedbackId +
                    ", 新状态=" + newStatus);

            boolean success = feedbackService.updateFeedbackStatus(feedbackId, newStatus);

            if (success) {
                return Response.success("反馈状态更新成功", null);
            } else {
                return Response.error(304, "反馈状态更新失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("更新反馈状态异常: " + e.getMessage());
            return Response.error(500, "更新失败，请稍后重试");
        }
    }

    /**
     * 获取学生的所有反馈记录
     * GET /api/feedback/student
     */
    @GetMapping("/student")
    public Response<?> getStudentFeedbacks(
            @RequestParam Integer studentId,
            @RequestParam(required = false) Integer processStatus,
            @RequestParam(required = false) Integer feedbackType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        try {
            if (studentId == null) {
                return Response.error(400, "学生ID不能为空");
            }

            System.out.println("获取学生反馈记录请求：studentId=" + studentId
                    + ", processStatus=" + processStatus
                    + ", feedbackType=" + feedbackType
                    + ", keyword=" + keyword
                    + ", page=" + page
                    + ", size=" + size);

            IPage<FeedBack> pageData = feedbackService.getStudentFeedbackPage(
                    studentId, processStatus, feedbackType, keyword, page, size
            );

            Map<String, Object> result = new HashMap<>();
            result.put("total", pageData.getTotal());
            result.put("feedbacks", pageData.getRecords());

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("获取学生反馈记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }


    /**
     * 获取管理员负责的所有反馈记录
     * GET /api/feedback/admin
     */
    @GetMapping("/admin")
    public Response<?> getAdminFeedbacks(@RequestParam Integer adminId) {
        try {
            if (adminId == null) {
                return Response.error(400, "管理员ID不能为空");
            }

            System.out.println("获取管理员反馈记录请求：管理员ID=" + adminId);

            List<FeedBack> feedbacks = feedbackService.getAdminFeedbacks(adminId);

            if (feedbacks == null || feedbacks.isEmpty()) {
                return Response.success("该管理员暂无负责的反馈记录", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", feedbacks.size());
            result.put("feedbacks", feedbacks);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("获取管理员反馈记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 根据时间范围查询反馈记录
     * GET /api/feedback/time-range
     */
    @GetMapping("/time-range")
    public Response<?> getTimeFeedbacks(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        try {
            System.out.println("按时间范围查询反馈记录：开始时间=" + startTime +
                    ", 结束时间=" + endTime);

            List<FeedBack> feedbacks = feedbackService.getTimeFeedbacks(startTime, endTime);

            if (feedbacks == null || feedbacks.isEmpty()) {
                return Response.success("该时间段内暂无反馈记录", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", feedbacks.size());
            result.put("feedbacks", feedbacks);
            result.put("startTime", startTime);
            result.put("endTime", endTime);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("按时间范围查询反馈记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 获取所有反馈记录（管理员用）
     * GET /api/feedback/all
     */
    @GetMapping("/all")
    public Response<?> getAllFeedbacks() {
        try {
            System.out.println("获取所有反馈记录请求");

            // 可以查询adminID为null的所有反馈
            List<FeedBack> feedbacks = feedbackService.getTimeFeedbacks(null, null);

            if (feedbacks == null || feedbacks.isEmpty()) {
                return Response.success("暂无反馈记录", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", feedbacks.size());
            result.put("feedbacks", feedbacks);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("获取所有反馈记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }
}
