package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.Notification;
import com.nbucs.studyroombackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 多条件查询通知记录
     * GET /api/notification/query
     */
    @GetMapping("/query")
    public Response<?> queryNotifications(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer notificationType,
            @RequestParam(required = false) Integer notificationStatus,
            @RequestParam(required = false) Integer adminId) {

        try {
            System.out.println("查询通知请求：学生ID=" + studentId +
                    ", 类型=" + notificationType +
                    ", 状态=" + notificationStatus +
                    ", 管理员ID=" + adminId);

            List<Notification> notifications = notificationService.queryNotifications(
                    studentId, notificationType, notificationStatus, adminId);

            if (notifications == null || notifications.isEmpty()) {
                return Response.success("没有找到符合条件的通知", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", notifications.size());
            result.put("notifications", notifications);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("查询通知记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 标记通知为已查看
     * PUT /api/notification/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public Response<?> markNotificationAsRead(@PathVariable String notificationId) {

        try {
            if (notificationId == null || notificationId.trim().isEmpty()) {
                return Response.error(400, "通知ID不能为空");
            }

            System.out.println("标记通知为已查看：通知ID=" + notificationId);

            boolean success = notificationService.markNotificationAsRead(notificationId);

            if (success) {
                return Response.success("标记为已查看成功", null);
            } else {
                return Response.error(304, "标记为已查看失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("标记通知为已查看异常: " + e.getMessage());
            return Response.error(500, "操作失败，请稍后重试");
        }
    }

    /**
     * 批量标记学生所有通知为已查看
     * PUT /api/notification/student/{studentId}/read-all
     */
    @PutMapping("/student/{studentId}/read-all")
    public Response<?> markStudentNotificationsAsRead(@PathVariable Integer studentId) {

        try {
            if (studentId == null) {
                return Response.error(400, "学生ID不能为空");
            }

            System.out.println("批量标记学生通知为已查看：学生ID=" + studentId);

            int markedCount = notificationService.markStudentNotificationsAsRead(studentId);

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("markedCount", markedCount);

            return Response.success("批量标记完成，成功标记" + markedCount + "条通知", result);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("批量标记通知异常: " + e.getMessage());
            return Response.error(500, "操作失败，请稍后重试");
        }
    }

    /**
     * 删除单条通知
     * DELETE /api/notification/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public Response<?> deleteNotificationById(@PathVariable String notificationId) {

        try {
            if (notificationId == null || notificationId.trim().isEmpty()) {
                return Response.error(400, "通知ID不能为空");
            }

            System.out.println("删除通知请求：通知ID=" + notificationId);

            boolean success = notificationService.deleteNotificationById(notificationId);

            if (success) {
                return Response.success("删除通知成功", null);
            } else {
                return Response.error(304, "删除通知失败，可能通知不存在");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("删除通知异常: " + e.getMessage());
            return Response.error(500, "操作失败，请稍后重试");
        }
    }

    /**
     * 删除学生所有通知
     * DELETE /api/notification/student/{studentId}
     */
    @DeleteMapping("/student/{studentId}")
    public Response<?> deleteNotificationsByStudentId(@PathVariable Integer studentId) {

        try {
            if (studentId == null) {
                return Response.error(400, "学生ID不能为空");
            }

            System.out.println("删除学生所有通知请求：学生ID=" + studentId);

            int deletedCount = notificationService.deleteNotificationsByStudentId(studentId);

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("deletedCount", deletedCount);

            return Response.success("删除完成，成功删除" + deletedCount + "条通知", result);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("删除学生通知异常: " + e.getMessage());
            return Response.error(500, "操作失败，请稍后重试");
        }
    }

    /**
     * 获取学生未读通知数量
     * GET /api/notification/student/{studentId}/unread-count
     */
    @GetMapping("/student/{studentId}/unread-count")
    public Response<?> getUnreadNotificationCount(@PathVariable Integer studentId) {

        try {
            if (studentId == null) {
                return Response.error(400, "学生ID不能为空");
            }

            System.out.println("获取学生未读通知数量：学生ID=" + studentId);

            // 查询未读通知（状态1为未读）
            List<Notification> unreadNotifications = notificationService.queryNotifications(
                    studentId, null, 1, null);

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("unreadCount", unreadNotifications != null ? unreadNotifications.size() : 0);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("获取未读通知数量异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }
}