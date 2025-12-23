package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.ViolationRecord;

import java.util.List;

public interface ViolationService {

    /**
     * 添加违规记录
     * @param violationRecord 违规记录
     * @return 创建的违规记录
     */
    ViolationRecord addViolationRecord(ViolationRecord violationRecord);

    /**
     * 查询所有违规记录
     * @return 违规记录列表
     */
    List<ViolationRecord> getAllViolationRecords();

    /**
     * 多条件查询违规记录
     * @param studentId 学生ID（可选）
     * @param adminId 管理员ID（可选）
     * @param status 状态（可选）
     * @param violationType 违规类型（可选）
     * @return 违规记录列表
     */
    List<ViolationRecord> queryViolationRecords(Integer studentId, Integer adminId,
                                                Integer status, Integer violationType);

    /**
     * 提交申诉
     * @param violationRecord 包含申诉信息的违规记录
     * @return 申诉是否成功
     */
    boolean submitAppeal(ViolationRecord violationRecord);

    /**
     * 处理申诉驳回
     * @param violationRecord 违规记录
     * @return 处理是否成功
     */
    boolean processAppealRejection(ViolationRecord violationRecord);

    /**
     * 处理申诉通过
     * @param violationRecord 违规记录
     * @return 处理是否成功
     */
    boolean processAppealApproval(ViolationRecord violationRecord);

    /**
     * 批量确认违规记录（修改状态为3-已确认）
     * @param violationRecordIds 违规记录ID列表
     * @return 成功更新的数量
     */
    int batchConfirmViolations(List<String> violationRecordIds);

    boolean deleteViolationRecord(String violationRecordId);

    int deleteViolationRecordByStudentId(Integer studentId);


}
