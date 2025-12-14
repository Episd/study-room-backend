package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Seat seat = seatService.getSeatById(reservationRecord.getSeatId());
        if (seat == null) {
            throw new RuntimeException("座位不存在");
        }

        // 3. 生成预约记录ID
        String reservationId = generateReservationId();
        reservationRecord.setReservationRecordId(reservationId);

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
    public ReservationRecord reserveSeminarRoom(ReservationRecord reservationRecord) {
        // 研讨室预约逻辑
        if (checkTimeConflict(reservationRecord)) {
            throw new RuntimeException("该时间段已被预约");
        }

        // 3. 生成预约记录ID
        String reservationId = generateReservationId();
        reservationRecord.setReservationRecordId(reservationId);

//        reservationRecord.setReservationRecordId(generateReservationId());
        reservationRecord.setReservationRecordStatus(1); // 自动审批
        reservationRecord.setCancelPermission(1); // 可取消
        reservationRecord.setCreateTime(LocalDateTime.now());

        int result = reservationRecordMapper.insert(reservationRecord);
        if (result <= 0) {
            throw new RuntimeException("研讨室预约失败");
        }

        return reservationRecord;
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
        if (reservationRecord.getStudentId() == null) {
            return false;
        }

        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("studentID", reservationRecord.getStudentId())
                .in("reservationRecordStatus", Arrays.asList(0, 1, 2))
                .and(wrapper -> wrapper
                        // 放宽边界：避免刚好相邻的时间被误判为重叠
                        .lt("reservationStartTime", reservationRecord.getReservationEndTime().minusSeconds(1))
                        .gt("reservationEndTime", reservationRecord.getReservationStartTime().plusSeconds(1))
                );

        // 排除自身
        if (reservationRecord.getReservationRecordId() != null) {
            queryWrapper.ne("reservationRecordID", reservationRecord.getReservationRecordId());
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
        boolean isSeatReservation = reservationRecord.getSeatId() != null;
        boolean isSeminarRoomReservation = reservationRecord.getSeminarRoomId() != null;

        if (isSeatReservation) {
            queryWrapper.eq("seatID", reservationRecord.getSeatId());
        } else if (isSeminarRoomReservation) {
            queryWrapper.eq("seminarRoomID", reservationRecord.getSeminarRoomId());
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
        if (reservationRecord.getReservationRecordId() != null) {
            queryWrapper.ne("reservationRecordID", reservationRecord.getReservationRecordId());
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

    // 查询指定座位在某个时间段的可用性
    public boolean isSeatAvailable(String seatId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<ReservationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seatID", seatId)
                .in("reservationRecordStatus", Arrays.asList(0, 1, 2)) // 有效的预约状态
                .and(wrapper ->
                        wrapper.lt("reservationStartTime", endTime)
                                .gt("reservationEndTime", startTime)
                );

        return reservationRecordMapper.selectCount(queryWrapper) == 0;
    }

    // 查询指定时间段内可用的座位
    public List<Seat> findAvailableSeats(LocalDateTime startTime, LocalDateTime endTime, String studyRoomId) {
        // 先找出该自习室的所有座位
        QueryWrapper<Seat> seatQuery = new QueryWrapper<>();
        if (studyRoomId != null) {
            seatQuery.eq("seatBelonging", studyRoomId);
        }
        List<Seat> allSeats = seatService.getAllSeats();

        // 过滤出在指定时间段内没有被预约的座位
        return allSeats.stream()
                .filter(seat -> isSeatAvailable(seat.getSeatId(), startTime, endTime))
                .collect(Collectors.toList());
    }

}