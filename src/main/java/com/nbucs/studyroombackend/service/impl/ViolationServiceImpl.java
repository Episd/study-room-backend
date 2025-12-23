package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.nbucs.studyroombackend.entity.ViolationRecord;
import com.nbucs.studyroombackend.mapper.ViolationRecordMapper;
import com.nbucs.studyroombackend.service.NotificationService;
import com.nbucs.studyroombackend.service.StudentService;
import com.nbucs.studyroombackend.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ViolationServiceImpl implements ViolationService {
    @Autowired
    private ViolationRecordMapper violationRecordMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private StudentService studentUserService;

    @Override
    @Transactional
    public ViolationRecord addViolationRecord(ViolationRecord violationRecord) {
        // 参数验证
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        if (violationRecord.getStudentID() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (violationRecord.getViolationType() == null) {
            throw new IllegalArgumentException("违规类型不能为空");
        }

        // 验证违规类型是否有效
        if (violationRecord.getViolationType() < 1 || violationRecord.getViolationType() > 6) {
            throw new IllegalArgumentException("违规类型无效，有效范围：1-6");
        }

        try {
            // 1. 生成违规记录ID
            String violationId = generateViolationRecordId();
            violationRecord.setViolationRecordID(violationId);

            // 2. 设置默认管理员ID（系统管理员ID为1）
            violationRecord.setAdminID(1);

            // 3. 根据违规类型设置扣除积分
            Integer deductPoints = getDeductPointsByType(violationRecord.getViolationType());
            violationRecord.setDeductPoints(deductPoints);

            // 4. 如果details为空，使用违规类型对应的文字
            if (violationRecord.getDetails() == null || violationRecord.getDetails().trim().isEmpty()) {
                violationRecord.setDetails(getViolationTypeText(violationRecord.getViolationType()));
            }

            // 5. 设置违规时间（如果未提供，使用当前时间）
            if (violationRecord.getViolationTime() == null) {
                violationRecord.setViolationTime(LocalDateTime.now());
            }

            // 6. 设置状态为2-待申诉
            violationRecord.setStatus(2);

            // 7. 设置创建时间
            violationRecord.setCreateTime(LocalDateTime.now());

            // 8. 申诉相关字段为空
            violationRecord.setAppealReason(null);
            violationRecord.setAppealTime(null);
            violationRecord.setProcessTime(null);

            // 9. 插入违规记录
            int insertResult = violationRecordMapper.insert(violationRecord);
            if (insertResult <= 0) {
                throw new RuntimeException("添加违规记录失败");
            }

            // 10. 扣除学生积分
            try {
                studentUserService.deductStudentPoints(violationRecord.getStudentID(), deductPoints);
                System.out.println("成功扣除学生[" + violationRecord.getStudentID() + "]积分" + deductPoints + "分");
            } catch (Exception e) {
                System.err.println("扣除学生积分失败，但违规记录已保存。错误：" + e.getMessage());
                // 积分扣除失败不影响违规记录的保存
            }

            // 11. 发送违规通知
            try {
                notificationService.sendViolationNotification(violationRecord);
                System.out.println("违规通知发送成功，违规ID：" + violationId);
            } catch (Exception e) {
                System.err.println("发送违规通知失败，但违规记录已保存。错误：" + e.getMessage());
                // 通知发送失败不影响违规记录的保存
            }

            return violationRecord;

        } catch (Exception e) {
            System.err.println("添加违规记录失败：" + e.getMessage());
            throw new RuntimeException("添加违规记录失败：" + e.getMessage(), e);
        }
    }

    /**
     * 根据违规类型获取扣除积分
     */
    private Integer getDeductPointsByType(Integer violationType) {
        switch (violationType) {
            case 1: return 20; // 违规占座
            case 2: return 5;  // 签到超时
            case 3: return 10; // 暂离超时
            case 4: return 15; // 研讨室人数不足
            case 5: return 1;  // 未签退
            case 6: return 2;  // 早退
            default: return 0;
        }
    }

    /**
     * 根据违规类型获取文字描述
     */
    private String getViolationTypeText(Integer violationType) {
        switch (violationType) {
            case 1: return "违规占座";
            case 2: return "签到超时";
            case 3: return "暂离超时";
            case 4: return "研讨室人数不足";
            case 5: return "未签退";
            case 6: return "早退";
            default: return "其他违规";
        }
    }

    /**
     * 生成违规记录ID（VR + 8位日期 + 4位序号）
     */
    private String generateViolationRecordId() {
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天已生成的违规记录数量
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("violationRecordID", "VR" + datePart)
                .orderByDesc("violationRecordID")
                .last("LIMIT 1");

        ViolationRecord lastRecord = violationRecordMapper.selectOne(queryWrapper);

        int sequence = 1;

        if (lastRecord != null && lastRecord.getViolationRecordID() != null) {
            String lastId = lastRecord.getViolationRecordID();
            if (lastId.startsWith("VR" + datePart) && lastId.length() >= 12) {
                try {
                    String seqStr = lastId.substring(10);
                    sequence = Integer.parseInt(seqStr) + 1;
                    if (sequence > 9999) {
                        sequence = 1;
                    }
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }

        String sequencePart = String.format("%04d", sequence);
        return "VR" + datePart + sequencePart;
    }

    @Override
    public List<ViolationRecord> getAllViolationRecords() {
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createTime"); // 按创建时间倒序排列

        return violationRecordMapper.selectList(queryWrapper);
    }

    @Override
    public List<ViolationRecord> queryViolationRecords(Integer studentId, Integer adminId,
                                                       Integer status, Integer violationType) {
        QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("createTime");

        // 条件判断：如果不为null，则添加查询条件
        if (studentId != null) {
            queryWrapper.eq("studentID", studentId);
        }

        if (adminId != null) {
            queryWrapper.eq("adminID", adminId);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        if (violationType != null) {
            queryWrapper.eq("violationType", violationType);
        }

        return violationRecordMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean submitAppeal(ViolationRecord violationRecord) {
        // 参数验证
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        if (violationRecord.getViolationRecordID() == null ||
                violationRecord.getViolationRecordID().trim().isEmpty()) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getAppealReason() == null ||
                violationRecord.getAppealReason().trim().isEmpty()) {
            throw new IllegalArgumentException("申诉理由不能为空");
        }

        // 查询记录是否存在
        ViolationRecord existingRecord = violationRecordMapper.selectById(violationRecord.getViolationRecordID());
        if (existingRecord == null) {
            throw new RuntimeException("违规记录不存在");
        }

        // 检查是否可以申诉（状态为1-已生成、2-待申诉时才可以申诉）
        if (existingRecord.getStatus() != null &&
                (existingRecord.getStatus() == 3 ||  // 已确认
                        existingRecord.getStatus() == 4 ||  // 申诉中
                        existingRecord.getStatus() == 5)) { // 已撤销
            throw new RuntimeException("该违规记录当前状态不允许申诉");
        }

        // 创建更新对象
        UpdateWrapper<ViolationRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("violationRecordID", violationRecord.getViolationRecordID())
                .set("status", 4) // 状态改为4（申诉中）
                .set("appealReason", violationRecord.getAppealReason().trim())
                .set("appealTime", LocalDateTime.now());

        // 执行更新
        int result = violationRecordMapper.update(null, updateWrapper);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean processAppealRejection(ViolationRecord violationRecord) {
        // 参数验证
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        if (violationRecord.getViolationRecordID() == null) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getAdminID() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        try {
            // 1. 查询违规记录是否存在
            ViolationRecord existingRecord = violationRecordMapper.selectById(violationRecord.getViolationRecordID());
            if (existingRecord == null) {
                throw new RuntimeException("违规记录不存在");
            }

            // 2. 检查当前状态是否为4（申诉中）
            if (existingRecord.getStatus() != 4) {
                throw new RuntimeException("当前状态不是申诉中，无法驳回申诉");
            }

            // 3. 更新状态为3（已确认）
            UpdateWrapper<ViolationRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("violationRecordID", violationRecord.getViolationRecordID())
                    .set("status", 3) // 状态改为3（已确认）
                    .set("adminID", violationRecord.getAdminID()) // 更新管理员ID
                    .set("processTime", LocalDateTime.now()); // 设置处理时间

            int result = violationRecordMapper.update(null, updateWrapper);

            // 4. 如果更新成功，发送申诉驳回通知
            if (result > 0) {
                try {
                    // 查询更新后的记录
                    ViolationRecord updatedRecord = violationRecordMapper.selectById(violationRecord.getViolationRecordID());
                    if (updatedRecord != null) {
                        // 发送申诉驳回通知
                        notificationService.sendAppealRejectedNotification(updatedRecord);
                        System.out.println("申诉驳回处理成功，已发送通知，违规ID：" + violationRecord.getViolationRecordID());
                    }
                } catch (Exception e) {
                    System.err.println("状态已更新为已确认，但申诉驳回通知发送失败，违规ID：" +
                            violationRecord.getViolationRecordID() + "，错误：" + e.getMessage());
                }
            }

            return result > 0;

        } catch (Exception e) {
            System.err.println("处理申诉驳回失败：" + e.getMessage());
            throw new RuntimeException("处理申诉驳回失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean processAppealApproval(ViolationRecord violationRecord) {
        // 参数验证
        if (violationRecord == null) {
            throw new IllegalArgumentException("违规记录不能为空");
        }

        if (violationRecord.getViolationRecordID() == null) {
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        if (violationRecord.getAdminID() == null) {
            throw new IllegalArgumentException("管理员ID不能为空");
        }

        try {
            // 1. 查询违规记录
            ViolationRecord existingRecord = violationRecordMapper.selectById(violationRecord.getViolationRecordID());
            if (existingRecord == null) {
                throw new RuntimeException("违规记录不存在");
            }

            // 2. 检查当前状态是否为4（申诉中）
            if (existingRecord.getStatus() != 4) {
                throw new RuntimeException("当前状态不是申诉中，无法通过申诉");
            }

            // 3. 恢复学生积分
            try {
                // 调用学生服务恢复积分（假设有addStudentPoints方法）
                Integer deductPoints = existingRecord.getDeductPoints();
                if (deductPoints != null && deductPoints > 0) {
                    // 恢复被扣除的积分
                    studentUserService.addStudentPoints(existingRecord.getStudentID(), deductPoints);
                    System.out.println("成功恢复学生[" + existingRecord.getStudentID() +
                            "]积分" + deductPoints + "分");
                }
            } catch (Exception e) {
                System.err.println("恢复学生积分失败，错误：" + e.getMessage());
                // 积分恢复失败应阻止状态更新，因为这是关键业务
                throw new RuntimeException("恢复学生积分失败：" + e.getMessage());
            }

            // 4. 更新状态为5（已撤销）
            UpdateWrapper<ViolationRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("violationRecordID", violationRecord.getViolationRecordID())
                    .set("status", 5) // 状态改为5（已撤销）
                    .set("adminID", violationRecord.getAdminID()) // 更新管理员ID
                    .set("processTime", LocalDateTime.now()); // 设置处理时间

            int result = violationRecordMapper.update(null, updateWrapper);

            // 5. 如果更新成功，发送申诉通过通知
            if (result > 0) {
                try {
                    // 查询更新后的记录
                    ViolationRecord updatedRecord = violationRecordMapper.selectById(violationRecord.getViolationRecordID());
                    if (updatedRecord != null) {
                        // 发送申诉通过通知（注意：应该是sendAppealApprovedNotification）
                        notificationService.sendAppealApprovedNotification(updatedRecord);
                        System.out.println("申诉通过处理成功，已发送通知，违规ID：" + violationRecord.getViolationRecordID());
                    }
                } catch (Exception e) {
                    System.err.println("状态已更新为已撤销，但申诉通过通知发送失败，违规ID：" +
                            violationRecord.getViolationRecordID() + "，错误：" + e.getMessage());
                }
            }

            return result > 0;

        } catch (Exception e) {
            System.err.println("处理申诉通过失败：" + e.getMessage());
            throw new RuntimeException("处理申诉通过失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int batchConfirmViolations(List<String> violationRecordIds) {
        // 参数验证
        if (violationRecordIds == null || violationRecordIds.isEmpty()) {
            throw new IllegalArgumentException("违规记录ID列表不能为空");
        }

        // 过滤空值
        List<String> validIds = violationRecordIds.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .toList();

        if (validIds.isEmpty()) {
            return 0;
        }

        // 创建更新对象
        UpdateWrapper<ViolationRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("violationRecordID", validIds)
                .set("status", 3) // 状态改为3（已确认）
                .set("processTime", LocalDateTime.now()); // 更新处理时间

        // 执行批量更新
        int result = violationRecordMapper.update(null, updateWrapper);
        return result;
    }

    @Override
    @Transactional
    public boolean deleteViolationRecord(String violationRecordId){
        if(violationRecordId == null || violationRecordId.trim().isEmpty()){
            throw new IllegalArgumentException("违规记录ID不能为空");
        }

        try{
            int result = violationRecordMapper.deleteById(violationRecordId);

            if(result > 0){
                System.out.println("删除违规记录成功，违规ID："+violationRecordId);
                return true;
            } else {
                System.out.println(("违规记录删除失败，违规ID："+violationRecordId));
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("删除违规失败："+e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int deleteViolationRecordByStudentId(Integer studentId){
        if(studentId == null){
            throw new IllegalArgumentException("学生ID不能为空");
        }

        try{
            QueryWrapper<ViolationRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("studentID", studentId);

            int result = violationRecordMapper.delete(queryWrapper);

            System.out.println("删除学生[" + studentId + "]的所有违规，成功删除" + result+ "条");

            return result;
        } catch (Exception e) {
            throw new RuntimeException("删除学生违规记录失败："+e.getMessage(), e);
        }
    }
}
