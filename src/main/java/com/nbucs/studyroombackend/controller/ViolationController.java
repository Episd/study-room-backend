package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.ViolationRecord;
import com.nbucs.studyroombackend.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/violation")
public class ViolationController {

    @Autowired
    private ViolationService violationRecordService;

    /**
     * 获取所有违规记录
     * GET /api/violation/all
     */
    @GetMapping("/all")
    public Response<?> getAllViolationRecords() {
        try {
            System.out.println("获取所有违规记录请求");

            List<ViolationRecord> violations = violationRecordService.getAllViolationRecords();

            if (violations == null || violations.isEmpty()) {
                return Response.success("暂无违规记录", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", violations.size());
            result.put("violations", violations);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("获取所有违规记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 多条件查询违规记录
     * GET /api/violation/query
     */
    @GetMapping("/query")
    public Response<?> queryViolationRecords(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer adminId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer violationType) {

        try {
            System.out.println("查询违规记录请求：学生ID=" + studentId +
                    ", 管理员ID=" + adminId +
                    ", 状态=" + status +
                    ", 违规类型=" + violationType);

            List<ViolationRecord> violations = violationRecordService.queryViolationRecords(
                    studentId, adminId, status, violationType);

            if (violations == null || violations.isEmpty()) {
                return Response.success("没有找到符合条件的违规记录", null);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", violations.size());
            result.put("violations", violations);

            return Response.success("查询成功", result);

        } catch (Exception e) {
            System.err.println("查询违规记录异常: " + e.getMessage());
            return Response.error(500, "查询失败，请稍后重试");
        }
    }

    /**
     * 提交申诉
     * PUT /api/violation/appeal
     */
    @PutMapping("/appeal")
    public Response<?> submitAppeal(@RequestBody ViolationRecord violationRecord) {
        try {
            if (violationRecord == null) {
                return Response.error(400, "申诉信息不能为空");
            }

            System.out.println("提交申诉请求：违规记录ID=" + violationRecord.getViolationRecordID());

            boolean success = violationRecordService.submitAppeal(violationRecord);

            if (success) {
                return Response.success("申诉提交成功", null);
            } else {
                return Response.error(304, "申诉提交失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("提交申诉异常: " + e.getMessage());
            return Response.error(500, "提交失败，请稍后重试");
        }
    }

    /**
     * 处理申诉驳回
     * PUT /api/violation/reject
     */
    @PutMapping("/reject")
    public Response<?> processAppealRejection(@RequestBody ViolationRecord violationRecord) {
        try {
            if (violationRecord == null) {
                return Response.error(400, "违规记录不能为空");
            }

            if (violationRecord.getViolationRecordID() == null) {
                return Response.error(400, "违规记录ID不能为空");
            }

            System.out.println("处理申诉驳回请求：违规记录ID=" + violationRecord.getViolationRecordID() +
                    ", 管理员ID=" + violationRecord.getAdminID());

            boolean success = violationRecordService.processAppealRejection(violationRecord);

            if (success) {
                return Response.success("申诉驳回处理成功", null);
            } else {
                return Response.error(304, "申诉驳回处理失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("处理申诉驳回异常: " + e.getMessage());
            return Response.error(500, "处理失败，请稍后重试");
        }
    }

    /**
     * 处理申诉通过
     * PUT /api/violation/approve
     */
    @PutMapping("/approve")
    public Response<?> processAppealApproval(@RequestBody ViolationRecord violationRecord) {
        try {
            if (violationRecord == null) {
                return Response.error(400, "违规记录不能为空");
            }

            if (violationRecord.getViolationRecordID() == null) {
                return Response.error(400, "违规记录ID不能为空");
            }

            System.out.println("处理申诉通过请求：违规记录ID=" + violationRecord.getViolationRecordID() +
                    ", 管理员ID=" + violationRecord.getAdminID());

            boolean success = violationRecordService.processAppealApproval(violationRecord);

            if (success) {
                return Response.success("申诉通过处理成功", null);
            } else {
                return Response.error(304, "申诉通过处理失败");
            }

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("处理申诉通过异常: " + e.getMessage());
            return Response.error(500, "处理失败，请稍后重试");
        }
    }

    /**
     * 批量确认违规记录
     * PUT /api/violation/batch-confirm
     */
    @PutMapping("/batch-confirm")
    public Response<?> batchConfirmViolations(@RequestBody List<String> violationRecordIds) {
        try {
            if (violationRecordIds == null || violationRecordIds.isEmpty()) {
                return Response.error(400, "违规记录ID列表不能为空");
            }

            System.out.println("批量确认违规记录请求：数量=" + violationRecordIds.size());

            int successCount = violationRecordService.batchConfirmViolations(violationRecordIds);

            Map<String, Object> result = new HashMap<>();
            result.put("total", violationRecordIds.size());
            result.put("successCount", successCount);
            result.put("failedCount", violationRecordIds.size() - successCount);

            return Response.success("批量确认完成", result);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("批量确认违规记录异常: " + e.getMessage());
            return Response.error(500, "处理失败，请稍后重试");
        }
    }

    /**
     * 添加违规记录
     * POST /api/violation/add
     */
    @PostMapping("/add")
    public Response<?> addViolationRecord(@RequestBody ViolationRecord violationRecord) {
        try {
            if (violationRecord == null) {
                return Response.error(400, "违规记录不能为空");
            }

            System.out.println("添加违规记录请求：学生ID=" + violationRecord.getStudentID() +
                    ", 违规类型=" + violationRecord.getViolationType());

            ViolationRecord savedRecord = violationRecordService.addViolationRecord(violationRecord);

            Map<String, Object> result = new HashMap<>();
            result.put("violationId", savedRecord.getViolationRecordID());
            result.put("deductPoints", savedRecord.getDeductPoints());
            result.put("message", "违规记录添加成功，已扣除" + savedRecord.getDeductPoints() + "积分");

            return Response.success("违规记录添加成功", result);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return Response.error(304, e.getMessage());
        } catch (Exception e) {
            System.err.println("添加违规记录异常: " + e.getMessage());
            return Response.error(500, "添加失败，请稍后重试");
        }
    }

}