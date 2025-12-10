package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.mapper.SeatMapper;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    // 座位状态常量（与数据库定义一致）
    public static final Integer STATUS_AVAILABLE = 0;      // 可预约
    public static final Integer STATUS_RESERVED = 1;       // 已预约
    public static final Integer STATUS_NOT_CHECKED_IN = 2; // 未签到
    public static final Integer STATUS_OCCUPIED = 3;       // 已占用
    public static final Integer STATUS_TEMPORARY_LEAVE = 4;// 暂离
    public static final Integer STATUS_MAINTENANCE = 5;    // 维修中

    // 座位类型常量
    public static final Integer TYPE_GENERAL = 0;          // 通用座位
    public static final Integer TYPE_SPECIAL = 1;          // 专用座位

    @Override
    public boolean addSeat(Seat seat) {
        // 1. 参数验证
        if (seat == null) {
            throw new IllegalArgumentException("座位信息不能为空");
        }

        // 2. 验证必填字段
        if (seat.getSeatId() == null || seat.getSeatId().trim().isEmpty()) {
            throw new IllegalArgumentException("座位ID不能为空");
        }

        if (seat.getSeatLocation() == null || seat.getSeatLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("座位位置不能为空");
        }

        if (seat.getSeatBelonging() == null || seat.getSeatBelonging().trim().isEmpty()) {
            throw new IllegalArgumentException("所属自习室不能为空");
        }

        // 3. 检查是否已存在
        Seat existing = seatMapper.selectById(seat.getSeatId());
        if (existing != null) {
            throw new RuntimeException("座位ID已存在：" + seat.getSeatId());
        }

        // 4. 设置默认值
        if (seat.getSeatType() == null) {
            seat.setSeatType(TYPE_GENERAL); // 默认通用座位
        }

        if (seat.getSeatStatus() == null) {
            seat.setSeatStatus(STATUS_AVAILABLE); // 默认可预约
        }

        // 5. 验证座位类型
        if (!isValidSeatType(seat.getSeatType())) {
            throw new IllegalArgumentException("无效的座位类型：" + seat.getSeatType());
        }

        // 6. 验证座位状态
        if (!isValidSeatStatus(seat.getSeatStatus())) {
            throw new IllegalArgumentException("无效的座位状态：" + seat.getSeatStatus());
        }

        // 7. 插入数据库
        int result = seatMapper.insert(seat);
        return result > 0;
    }

    @Override
    public boolean updateSeat(Seat seat) {
        // 1. 参数验证
        if (seat == null || seat.getSeatId() == null) {
            throw new IllegalArgumentException("座位ID不能为空");
        }

        // 2. 检查记录是否存在
        Seat existing = seatMapper.selectById(seat.getSeatId());
        if (existing == null) {
            throw new RuntimeException("座位不存在：" + seat.getSeatId());
        }

        // 3. 验证类型（如果提供了）
        if (seat.getSeatType() != null && !isValidSeatType(seat.getSeatType())) {
            throw new IllegalArgumentException("无效的座位类型：" + seat.getSeatType());
        }

        // 4. 验证状态（如果提供了）
        if (seat.getSeatStatus() != null && !isValidSeatStatus(seat.getSeatStatus())) {
            throw new IllegalArgumentException("无效的座位状态：" + seat.getSeatStatus());
        }

        // 5. 更新数据库（只更新非null字段）
        int result = seatMapper.updateById(seat);
        return result > 0;
    }

    @Override
    public boolean deleteSeat(Seat seat) {
        if (seat == null || seat.getSeatId() == null) {
            throw new IllegalArgumentException("座位ID不能为空");
        }
        return deleteSeat(seat.getSeatId());
    }

    @Override
    public boolean deleteSeat(String seatId) {
        if (seatId == null || seatId.trim().isEmpty()) {
            throw new IllegalArgumentException("座位ID不能为空");
        }

        // 检查座位是否被占用（除了空闲和维修中状态）
        Seat seat = seatMapper.selectById(seatId);
        if (seat != null && !Objects.equals(seat.getSeatStatus(), STATUS_AVAILABLE) &&
                !Objects.equals(seat.getSeatStatus(), STATUS_MAINTENANCE)) {
            throw new RuntimeException("座位当前被占用，无法删除");
        }

        int result = seatMapper.deleteById(seatId);
        return result > 0;
    }

    @Override
    public List<Seat> getAllSeats() {
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Seat::getSeatBelonging)  // 先按自习室排序
                .orderByAsc(Seat::getSeatId);        // 再按座位ID排序
        return seatMapper.selectList(queryWrapper);
    }

    @Override
    public Seat getSeatById(String seatId) {
        if (seatId == null || seatId.trim().isEmpty()) {
            throw new IllegalArgumentException("座位ID不能为空");
        }
        return seatMapper.selectById(seatId);
    }

    @Override
    public List<Seat> getSeatsByStudyRoom(String studyRoomId) {
        if (studyRoomId == null || studyRoomId.trim().isEmpty()) {
            throw new IllegalArgumentException("自习室ID不能为空");
        }

        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getSeatBelonging, studyRoomId)
                .orderByAsc(Seat::getSeatId);
        return seatMapper.selectList(queryWrapper);
    }

    @Override
    public List<Seat> getSeatsByStatus(Integer status) {
        if (!isValidSeatStatus(status)) {
            throw new IllegalArgumentException("无效的座位状态：" + status);
        }

        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getSeatStatus, status)
                .orderByAsc(Seat::getSeatBelonging)
                .orderByAsc(Seat::getSeatId);
        return seatMapper.selectList(queryWrapper);
    }

    @Override
    public List<Seat> getSeatsByType(Integer type) {
        if (!isValidSeatType(type)) {
            throw new IllegalArgumentException("无效的座位类型：" + type);
        }

        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getSeatType, type)
                .orderByAsc(Seat::getSeatBelonging)
                .orderByAsc(Seat::getSeatId);
        return seatMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateSeatStatus(String seatId, Integer status) {
        if (seatId == null || seatId.trim().isEmpty()) {
            throw new IllegalArgumentException("座位ID不能为空");
        }

        if (!isValidSeatStatus(status)) {
            throw new IllegalArgumentException("无效的座位状态：" + status);
        }

        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatStatus(status);

        return updateSeat(seat);
    }

    @Override
    public boolean isSeatAvailable(String seatId) {
        Seat seat = getSeatById(seatId);
        if (seat == null) {
            return false;
        }

        // 只有状态为0（可预约）的座位才可用
        return Objects.equals(seat.getSeatStatus(), STATUS_AVAILABLE);
    }

    @Override
    public List<Seat> searchSeatsByLocation(String locationKeyword) {
        if (locationKeyword == null || locationKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Seat::getSeatLocation, locationKeyword)
                .orderByAsc(Seat::getSeatBelonging)
                .orderByAsc(Seat::getSeatId);
        return seatMapper.selectList(queryWrapper);
    }

    // 新增：批量更新座位状态
    public boolean batchUpdateSeatStatus(List<String> seatIds, Integer status) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("座位ID列表不能为空");
        }

        if (!isValidSeatStatus(status)) {
            throw new IllegalArgumentException("无效的座位状态：" + status);
        }

        int successCount = 0;
        for (String seatId : seatIds) {
            try {
                if (updateSeatStatus(seatId, status)) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录日志，继续处理其他座位
                System.err.println("更新座位状态失败：" + seatId + " - " + e.getMessage());
            }
        }

        return successCount > 0;
    }

    // 新增：获取自习室的所有可用座位
    public List<Seat> getAvailableSeatsByStudyRoom(String studyRoomId) {
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getSeatBelonging, studyRoomId)
                .eq(Seat::getSeatStatus, STATUS_AVAILABLE)
                .orderByAsc(Seat::getSeatId);
        return seatMapper.selectList(queryWrapper);
    }

    // 新增：验证座位状态转移是否合法 - 使用 if-else 替代 switch
    public boolean isValidStatusTransition(Integer currentStatus, Integer newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        // 定义允许的状态转移规则
        if (currentStatus.equals(STATUS_AVAILABLE)) {
            // 可预约 → 已预约/维修中
            return newStatus.equals(STATUS_RESERVED) || newStatus.equals(STATUS_MAINTENANCE);
        } else if (currentStatus.equals(STATUS_RESERVED)) {
            // 已预约 → 未签到/已取消
            return newStatus.equals(STATUS_NOT_CHECKED_IN) || newStatus.equals(STATUS_AVAILABLE);
        } else if (currentStatus.equals(STATUS_NOT_CHECKED_IN)) {
            // 未签到 → 已占用/已取消
            return newStatus.equals(STATUS_OCCUPIED) || newStatus.equals(STATUS_AVAILABLE);
        } else if (currentStatus.equals(STATUS_OCCUPIED)) {
            // 已占用 → 暂离/已结束
            return newStatus.equals(STATUS_TEMPORARY_LEAVE) || newStatus.equals(STATUS_AVAILABLE);
        } else if (currentStatus.equals(STATUS_TEMPORARY_LEAVE)) {
            // 暂离 → 已占用/已结束
            return newStatus.equals(STATUS_OCCUPIED) || newStatus.equals(STATUS_AVAILABLE);
        } else if (currentStatus.equals(STATUS_MAINTENANCE)) {
            // 维修中 → 可预约
            return newStatus.equals(STATUS_AVAILABLE);
        } else {
            return false;
        }
    }

    // 辅助方法：验证座位类型
    private boolean isValidSeatType(Integer type) {
        return type != null && (type.equals(TYPE_GENERAL) || type.equals(TYPE_SPECIAL));
    }

    // 辅助方法：验证座位状态
    private boolean isValidSeatStatus(Integer status) {
        return status != null && status >= STATUS_AVAILABLE && status <= STATUS_MAINTENANCE;
    }

    // 获取座位状态描述 - 使用 if-else 替代 switch
    public String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        if (status.equals(STATUS_AVAILABLE)) {
            return "可预约";
        } else if (status.equals(STATUS_RESERVED)) {
            return "已预约";
        } else if (status.equals(STATUS_NOT_CHECKED_IN)) {
            return "未签到";
        } else if (status.equals(STATUS_OCCUPIED)) {
            return "已占用";
        } else if (status.equals(STATUS_TEMPORARY_LEAVE)) {
            return "暂离";
        } else if (status.equals(STATUS_MAINTENANCE)) {
            return "维修中";
        } else {
            return "未知状态";
        }
    }

    // 获取座位类型描述 - 使用 if-else 替代 switch
    public String getTypeDescription(Integer type) {
        if (type == null) {
            return "未知类型";
        }

        if (type.equals(TYPE_GENERAL)) {
            return "通用座位";
        } else if (type.equals(TYPE_SPECIAL)) {
            return "专用座位";
        } else {
            return "未知类型";
        }
    }

}