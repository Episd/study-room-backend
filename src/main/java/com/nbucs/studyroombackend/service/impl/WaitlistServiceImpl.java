package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.WaitlistRecord;
import com.nbucs.studyroombackend.mapper.WaitlistRecordMapper;
import com.nbucs.studyroombackend.service.ReservationService;
import com.nbucs.studyroombackend.service.WaitlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRecordMapper waitlistRecordMapper;

    @Autowired
    private ReservationService reservationService;

    @Override
    public WaitlistRecord waitSeat(ReservationRecord reservationRecord) {
        // 生成候补记录ID
        String id = generateWaitlistId();

        // 创建候补记录
        WaitlistRecord waitlistRecord = new WaitlistRecord();
        waitlistRecord.setWaitListRecordId(id);
        waitlistRecord.setStudentId(reservationRecord.getStudentId());
        waitlistRecord.setStudyRoomId(reservationRecord.getStudyRoomId());
        waitlistRecord.setSeatId(reservationRecord.getSeatId());
        waitlistRecord.setWaitListStartTime(reservationRecord.getReservationStartTime());
        waitlistRecord.setWaitListEndTime(reservationRecord.getReservationEndTime());
        waitlistRecord.setWaitListStatus(0); // 候补中
        waitlistRecord.setCancelPermission(1);
        waitlistRecord.setPriority(0); // 默认优先级
        waitlistRecord.setCreateTime(LocalDateTime.now());

        // 使用Mapper插入数据
        int result = waitlistRecordMapper.insert(waitlistRecord);
        if (result <= 0) {
            throw new RuntimeException("候补记录保存失败");
        }
        return waitlistRecord;
    }

    // 生成ID：WL202512140001
    private String generateWaitlistId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询今天已有的记录数
        LambdaQueryWrapper<WaitlistRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(WaitlistRecord::getWaitListRecordId, "WL" + date);

        // 使用Mapper查询数量
        Long count = waitlistRecordMapper.selectCount(wrapper);

        // 生成序号
        int num = (count != null ? count.intValue() : 0) + 1;
        String serial = String.format("%04d", num);

        return "WL" + date + serial;
    }

    @Override
    public WaitlistRecord waitSeminarRoom(ReservationRecord reservationRecord){
        // 生成候补记录ID
        String id = generateWaitlistId();

        // 创建候补记录
        WaitlistRecord waitlistRecord = new WaitlistRecord();
        waitlistRecord.setWaitListRecordId(id);
        waitlistRecord.setStudentId(reservationRecord.getStudentId());
        waitlistRecord.setSeminarRoomId(reservationRecord.getSeminarRoomId());
        waitlistRecord.setWaitListStartTime(reservationRecord.getReservationStartTime());
        waitlistRecord.setWaitListEndTime(reservationRecord.getReservationEndTime());
        waitlistRecord.setWaitListStatus(0); // 候补中
        waitlistRecord.setCancelPermission(1);
        waitlistRecord.setPriority(0); // 默认优先级
        waitlistRecord.setCreateTime(LocalDateTime.now());

        // 使用Mapper插入数据
        int result = waitlistRecordMapper.insert(waitlistRecord);
        if (result <= 0) {
            throw new RuntimeException("候补记录保存失败");
        }
        return waitlistRecord;
    }

    @Override
    public List<WaitlistRecord> checkWaitlist(Integer studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<WaitlistRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaitlistRecord::getStudentId, studentId)
                .orderByDesc(WaitlistRecord::getPriority)  // 按优先级降序
                .orderByDesc(WaitlistRecord::getCreateTime); // 再按创建时间降序

        return waitlistRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public boolean cancelWaitlist(String waitlistRecordId) {
        if (waitlistRecordId == null || waitlistRecordId.trim().isEmpty()) {
            return false;
        }

        // 直接查询并更新
        LambdaQueryWrapper<WaitlistRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaitlistRecord::getWaitListRecordId, waitlistRecordId)
                .in(WaitlistRecord::getWaitListStatus, 0, 1) // 只取消候补中(0)和已通过(1)的记录
                .eq(WaitlistRecord::getCancelPermission, 1); // 且允许取消

        WaitlistRecord updateRecord = new WaitlistRecord();
        updateRecord.setWaitListStatus(3); // 更新为已取消

        int result = waitlistRecordMapper.update(updateRecord, wrapper);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean confirmWaitlist(String waitlistRecordId) {
        if (waitlistRecordId == null || waitlistRecordId.trim().isEmpty()) {
            return false;
        }

        // 1. 查询候补记录
        WaitlistRecord waitlistRecord = waitlistRecordMapper.selectById(waitlistRecordId);
        if (waitlistRecord == null) {
            return false;
        }

        // 2. 检查状态是否允许确认（只有候补中可以确认）
        if (waitlistRecord.getWaitListStatus() != 0) { // 0-候补中
            return false;
        }

        try {
            // 3. 创建预约记录
            ReservationRecord reservationRecord = new ReservationRecord();

            // 设置学生ID
            reservationRecord.setStudentId(waitlistRecord.getStudentId());

            // 设置预约时间（使用候补时间）
            reservationRecord.setReservationStartTime(waitlistRecord.getWaitListStartTime());
            reservationRecord.setReservationEndTime(waitlistRecord.getWaitListEndTime());

            // 根据候补类型创建不同的预约记录
            if (waitlistRecord.getStudyRoomId() != null && waitlistRecord.getSeatId() != null) {
                // 自习室座位候补
                reservationRecord.setStudyRoomId(waitlistRecord.getStudyRoomId());
                reservationRecord.setSeatId(waitlistRecord.getSeatId());

                // 调用预约座位方法
                ReservationRecord reserved = reservationService.reserveSeat(reservationRecord);
                if (reserved == null) {
                    throw new RuntimeException("预约座位失败");
                }
            } else if (waitlistRecord.getSeminarRoomId() != null) {
                // 研讨室候补
                reservationRecord.setSeminarRoomId(waitlistRecord.getSeminarRoomId());
                reservationRecord.setSeminarRoomNum(waitlistRecord.getSeminarRoomNum());

                // 调用预约研讨室方法
                ReservationRecord reserved = reservationService.reserveSeminarRoom(reservationRecord);
                if (reserved == null) {
                    throw new RuntimeException("预约研讨室失败");
                }
            } else {
                // 候补记录类型不明确
                return false;
            }

            // 4. 更新候补记录状态为已通过(1)
            waitlistRecord.setWaitListStatus(1); // 1-已通过
            int updateResult = waitlistRecordMapper.updateById(waitlistRecord);

            return updateResult > 0;

        } catch (Exception e) {
            // 记录日志
            System.err.println("确认候补失败: " + e.getMessage());
            throw new RuntimeException("确认候补失败: " + e.getMessage(), e);
        }
    }
}
