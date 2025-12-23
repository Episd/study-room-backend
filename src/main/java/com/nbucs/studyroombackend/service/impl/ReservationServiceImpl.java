package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.nbucs.studyroombackend.dto.request.OccupiedTimeSlotQueryDto;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.mapper.ReservationRecordMapper;
import com.nbucs.studyroombackend.service.ReservationService;
import com.nbucs.studyroombackend.service.SeatService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {  // 移除 abstract

    @Autowired
    private ReservationRecordMapper reservationRecordMapper;

    @Autowired
    private SeatService seatService;

    @Override
    public ReservationRecord reserveSeat(ReservationRecord reservationRecord) {
        // 1. 检查时间段冲突 - 基于预约记录
        if (checkTimeConflict(reservationRecord)) {
            throw new RuntimeException("该时间段已被预约");
        }

        // 2. 检查座位是否存在（不检查座位状态，因为状态可能滞后）
        Seat seat = seatService.getSeatById(reservationRecord.getSeatID());
        if (seat == null) {
            throw new RuntimeException("座位不存在");
        }

        // 3. 生成预约记录ID
        String reservationId = generateReservationId();
        reservationRecord.setReservationRecordID(reservationId);

        // 3. 设置预约信息
//        reservationRecord.setReservationRecordId(generateReservationId());
        reservationRecord.setReservationRecordStatus(1); // 自动审批
        reservationRecord.setCancelPermission(1); // 可取消
        reservationRecord.setCreateTime(LocalDateTime.now());

        // 4. 保存预约记录到数据库
        int insertResult = reservationRecordMapper.insert(reservationRecord);
        if (insertResult <= 0) {
            throw new RuntimeException("预约保存失败");
        }

        return reservationRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reserveSeminarRoom(List<ReservationRecord> reservationRecords) {
        if (reservationRecords == null || reservationRecords.isEmpty()) {
            throw new IllegalArgumentException("预约请求列表不能为空");
        }

        // 获取第一个记录的信息用于验证
        ReservationRecord firstRecord = reservationRecords.get(0);

        // 检查时间段是否被占用
        if (checkTimeConflict(firstRecord)) {
            throw new RuntimeException("该研讨室在该时间段已被预约");
        }

        // 验证所有记录的基本信息
        Long seminarRoomId = firstRecord.getStudyRoomID();
        LocalDateTime startTime = firstRecord.getReservationStartTime();
        LocalDateTime endTime = firstRecord.getReservationEndTime();

        for (int i = 0; i < reservationRecords.size(); i++) {
            ReservationRecord record = reservationRecords.get(i);

            // 验证学生ID
            if (record.getStudentID() == null) {
                throw new IllegalArgumentException("第" + (i+1) + "个记录的学生ID不能为空");
            }

            // 验证时间一致性（可选）
            if (!record.getReservationStartTime().equals(startTime) ||
                    !record.getReservationEndTime().equals(endTime)) {
                throw new IllegalArgumentException("所有预约记录的时间段必须相同");
            }

            // 验证是否是同一个研讨室
            if (!record.getStudyRoomID().equals(seminarRoomId)) {
                throw new IllegalArgumentException("所有预约必须是同一个研讨室");
            }
        }

        try {
            // 为每个学生生成预约记录
            for (int i = 0; i < reservationRecords.size(); i++) {
                ReservationRecord record = reservationRecords.get(i);

                // 生成预约记录ID
                String reservationId = generateReservationId();
                record.setReservationRecordID(reservationId);

                // 设置预约状态
                record.setReservationRecordStatus(1); // 自动审批

                // 只有第一个学生有取消权限
                record.setCancelPermission(i == 0 ? 1 : 0);

                // 设置创建时间
                record.setCreateTime(LocalDateTime.now());

                // 插入数据库
                int result = reservationRecordMapper.insert(record);
                if (result <= 0) {
                    return false; // 任何一条记录插入失败，整个事务会回滚
                }
            }

            return true; // 所有记录都插入成功

        } catch (Exception e) {
            // 异常会被@Transactional捕获并回滚
            throw new RuntimeException("研讨室预约失败: " + e.getMessage());
        }
    }

    @Override
    public boolean checkTimeConflict(ReservationRecord reservationRecord) {
        // 1. 检查个人时间冲突（学生不能在同一时间段预约多个）
        boolean personalConflict = checkPersonalTimeConflict(reservationRecord);

        // 2. 检查资源时间冲突（座位/研讨室不能在同一时间段被预约多次）
        boolean resourceConflict = checkResourceTimeConflict(reservationRecord);

        return personalConflict || resourceConflict;
    }

    /**
     * 检查个人时间冲突：一个学生不能在重叠时间段有多个预约
     */
    private boolean checkPersonalTimeConflict(ReservationRecord reservationRecord) {
        if (reservationRecord.getStudentID() == null) {
            return false;
        }

        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentID", reservationRecord.getStudentID())
                .in("reservationRecordStatus", Arrays.asList(0, 1, 2))
                .and(wrapper -> wrapper
                        // 放宽边界：避免刚好相邻的时间被误判为重叠
                        .lt("reservationStartTime", reservationRecord.getReservationEndTime().minusSeconds(1))
                        .gt("reservationEndTime", reservationRecord.getReservationStartTime().plusSeconds(1))
                );

        // 排除自身
        if (reservationRecord.getReservationRecordID() != null) {
            queryWrapper.ne("reservationRecordID", reservationRecord.getReservationRecordID());
        }

        long conflictCount = reservationRecordMapper.selectCount(queryWrapper);
        return conflictCount > 0;
    }

    /**
     * 检查资源时间冲突：一个座位/研讨室不能在重叠时间段被预约多次
     */
    private boolean checkResourceTimeConflict(ReservationRecord reservationRecord) {
        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();

        // 确定要检查的资源类型
        boolean isSeatReservation = reservationRecord.getSeatID() != null;
        boolean isSeminarRoomReservation = reservationRecord.getSeminarRoomID() != null;

        if (isSeatReservation) {
            queryWrapper.eq("seatID", reservationRecord.getSeatID());
        } else if (isSeminarRoomReservation) {
            queryWrapper.eq("seminarRoomID", reservationRecord.getSeminarRoomID());
        } else {
            // 既不是座位也不是研讨室预约
            return false;
        }

        queryWrapper.in("reservationRecordStatus", Arrays.asList(0, 1, 2)) // 待审批、已通过、已开始
                .and(wrapper -> wrapper
                        // 检查时间重叠
                        .lt("reservationStartTime", reservationRecord.getReservationEndTime())
                        .gt("reservationEndTime", reservationRecord.getReservationStartTime())
                );

        // 排除自身（如果是更新操作）
        if (reservationRecord.getReservationRecordID() != null) {
            queryWrapper.ne("reservationRecordID", reservationRecord.getReservationRecordID());
        }

        long conflictCount = reservationRecordMapper.selectCount(queryWrapper);
        return conflictCount > 0;
    }

    /**
     * 生成预约记录ID - 改进版
     * 避免并发问题，确保序号不重复
     */
    private String generateReservationId() {
        // 获取当前日期
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询今天最大的序号
        Integer maxSequence = getTodayMaxSequence(dateStr);

        // 生成新序号
        int newSequence = (maxSequence == null ? 0 : maxSequence) + 1;

        // 格式化序号为4位数字
        String sequenceStr = String.format("%04d", newSequence);

        return "RR" + dateStr + sequenceStr;
    }

    /**
     * 获取今天最大的序号
     */
    private Integer getTodayMaxSequence(String dateStr) {
        // 查询今天所有ID
        List<String> todayIds = reservationRecordMapper.selectTodayReservationIds(dateStr);

        if (todayIds == null || todayIds.isEmpty()) {
            return null;
        }

        // 提取并找到最大序号
        return todayIds.stream()
                .map(id -> {
                    // 从ID中提取序号部分（最后4位）
                    if (id != null && id.length() >= 4) {
                        try {
                            return Integer.parseInt(id.substring(id.length() - 4));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public boolean acceptReservation(String reservationRecordId) {
        ReservationRecord record = reservationRecordMapper.selectById(reservationRecordId);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 在批准前再次检查时间冲突（防止在待审批期间被其他预约占用）
        if (checkTimeConflict(record)) {
            throw new RuntimeException("该时间段已被其他预约占用，无法批准");
        }

        // 更新预约状态为已通过
        record.setReservationRecordStatus(1); // 已通过
        boolean updateResult = reservationRecordMapper.updateById(record) > 0;

        return updateResult;
    }

    @Override
    public boolean cancelReservation(String reservationRecordId) {
        ReservationRecord record = reservationRecordMapper.selectById(reservationRecordId);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }

        if (record.getCancelPermission() == 0) {
            throw new RuntimeException("该预约不可取消");
        }

        // 更新预约状态为已取消
        record.setReservationRecordStatus(4);
        return reservationRecordMapper.updateById(record) > 0;
    }

    @Override
    public List<ReservationRecord> checkReservationRecord(Integer studentId) {
        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentID", studentId)
                .orderByDesc("reservationStartTime");
        return reservationRecordMapper.selectList(queryWrapper);
    }

    @Override
    public ReservationRecord getEarliestTodayReservation(String studentId) {
        // 参数验证
        if (StringUtils.isBlank(studentId)) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        try {
            ReservationRecord record = reservationRecordMapper.selectEarliestTodayReservation(studentId);
            return record;
        } catch (Exception e) {
            throw new RuntimeException("查询预约记录失败: " + e.getMessage());
        }
    }

    @Override
    public List<ReservationRecord> getOccupiedTimeSlots(OccupiedTimeSlotQueryDto queryDto) {
        if (queryDto == null || queryDto.getQueryDate() == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }

        LocalDate queryDate = queryDto.getQueryDate();
        LocalDateTime startTime = queryDate.atStartOfDay();
        LocalDateTime endTime = queryDate.atTime(23, 59, 59);

        // 使用QueryWrapper构建查询条件
        QueryWrapper<ReservationRecord> wrapper = new QueryWrapper<>();

        // 日期条件：查询当天的预约
        wrapper.between("reservationStartTime", startTime, endTime);

        // 状态条件：已通过(1)或已开始(2)
        wrapper.in("reservationRecordStatus", Arrays.asList(1, 2));

        // 自习室相关条件
        if (queryDto.getStudyRoomId() != null && !queryDto.getStudyRoomId().trim().isEmpty()) {
            wrapper.eq("studyRoomID", queryDto.getStudyRoomId());

            if (queryDto.getSeatId() != null && !queryDto.getSeatId().trim().isEmpty()) {
                wrapper.eq("seatID", queryDto.getSeatId());
            }
        }

        // 研讨室条件
        if (queryDto.getSeminarRoomId() != null && !queryDto.getSeminarRoomId().trim().isEmpty()) {
            wrapper.eq("seminarRoomID", queryDto.getSeminarRoomId());
        }

        // 排序
        wrapper.orderByAsc("reservationStartTime");

        // 打印最终SQL（更详细的方法）
        try {
            // 获取SQL语句的另一种方式
            String sql = wrapper.getSqlSegment();
        } catch (Exception e) {
            System.out.println("获取SQL信息失败: " + e.getMessage());
        }

        // 执行查询
        System.out.println("开始执行查询...");
        List<ReservationRecord> result = reservationRecordMapper.selectList(wrapper);

        System.out.println("查询结果数量: " + result.size());
        if (!result.isEmpty()) {
            System.out.println("查询到的记录:");
            for (int i = 0; i < result.size(); i++) {
                ReservationRecord record = result.get(i);
            }
        } else {
            System.out.println("未查询到任何记录");
        }

        return result;
    }

    @Override
    public boolean updateReservationStatus(String reservationId,Integer reservationStatus){
        ReservationRecord record = reservationRecordMapper.selectById(reservationId);
        if (record == null) {
            return false;
        }

        // 创建新的记录对象，只更新状态字段
        ReservationRecord updateRecord = new ReservationRecord();
        updateRecord.setReservationRecordID(reservationId);
        updateRecord.setReservationRecordStatus(reservationStatus);

        // 执行更新
        int rows = reservationRecordMapper.updateById(updateRecord);
        return rows > 0;
    }

    @Override
    public List<ReservationRecord> queryReservationRecords(Integer studentID,
                                                           LocalDate reservationDate,
                                                           Long studyRoomID,
                                                           Long seminarRoomID,
                                                           Integer reservationRecordStatus) {

        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();

        // 按创建时间倒序排列
        queryWrapper.orderByDesc("createTime");

        // 学号查询
        if (studentID != null) {
            queryWrapper.eq("studentID", studentID);
        }

        // 预约日期查询
        if (reservationDate != null) {
            LocalDateTime startOfDay = reservationDate.atStartOfDay();
            LocalDateTime endOfDay = reservationDate.atTime(LocalTime.MAX);
            queryWrapper.ge("reservationStartTime", startOfDay)
                    .le("reservationStartTime", endOfDay);
        }

        // 自习室ID查询
        if (studyRoomID != null) {
            queryWrapper.eq("studyRoomID", studyRoomID);
        }

        // 研讨室ID查询
        if (seminarRoomID != null) {
            queryWrapper.eq("seminarRoomID", seminarRoomID);
        }

        // 预约记录状态查询
        if (reservationRecordStatus != null) {
            queryWrapper.eq("reservationRecordStatus", reservationRecordStatus);
        }

        return reservationRecordMapper.selectList(queryWrapper);
    }

}