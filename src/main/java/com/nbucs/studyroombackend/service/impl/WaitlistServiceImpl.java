package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        waitlistRecord.setStudentId(reservationRecord.getStudentID());
        waitlistRecord.setStudyRoomId(reservationRecord.getStudyRoomID());
        waitlistRecord.setSeatId(reservationRecord.getSeatID());
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
        waitlistRecord.setStudentId(reservationRecord.getStudentID());
        waitlistRecord.setSeminarRoomId(reservationRecord.getSeminarRoomID());
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
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmWaitlist(String waitlistRecordId) {
        if (waitlistRecordId == null || waitlistRecordId.trim().isEmpty()) {
            return false;
        }

        // 1. 查询主候补记录
        WaitlistRecord mainWaitlist = waitlistRecordMapper.selectById(waitlistRecordId);
        if (mainWaitlist == null || mainWaitlist.getWaitListStatus() != 0) {
            return false;
        }

        try {
            boolean reservationSuccess = false;

            // 判断预约类型
            if (mainWaitlist.getStudyRoomId() != null && mainWaitlist.getSeatId() != null) {
                // 自习室座位候补（单学生）
                ReservationRecord reservationRecord = new ReservationRecord();
                reservationRecord.setStudentID(mainWaitlist.getStudentId());
                reservationRecord.setReservationStartTime(mainWaitlist.getWaitListStartTime());
                reservationRecord.setReservationEndTime(mainWaitlist.getWaitListEndTime());
                reservationRecord.setStudyRoomID(mainWaitlist.getStudyRoomId());
                reservationRecord.setSeatID(mainWaitlist.getSeatId());

                // 调用自习室座位预约方法
                ReservationRecord result = reservationService.reserveSeat(reservationRecord);
                reservationSuccess = result != null;

                if (reservationSuccess) {
                    mainWaitlist.setWaitListStatus(1);
                    return waitlistRecordMapper.updateById(mainWaitlist) > 0;
                }

            } else if (mainWaitlist.getSeminarRoomId() != null) {
                // 研讨室候补（多学生）

                // 2. 查找同一时间同一研讨室的其他候补记录
                List<WaitlistRecord> groupWaitlists = findGroupWaitlists(mainWaitlist);

                // 3. 创建预约记录列表
                List<ReservationRecord> reservationRecords = createReservationRecords(groupWaitlists);

                // 4. 调用研讨室预约方法
                reservationSuccess = reservationService.reserveSeminarRoom(reservationRecords);

                // 5. 如果成功，更新所有相关候补记录状态
                if (reservationSuccess) {
                    updateGroupWaitlistStatus(groupWaitlists);
                    return true;
                }

            } else {
                return false;
            }

            return false;

        } catch (Exception e) {
            System.err.println("确认候补失败: " + e.getMessage());
            throw new RuntimeException("确认候补失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查找同一组的候补记录
     */
    private List<WaitlistRecord> findGroupWaitlists(WaitlistRecord mainWaitlist) {
        List<WaitlistRecord> groupWaitlists = new ArrayList<>();
        groupWaitlists.add(mainWaitlist);

        // 查找同一时间同一研讨室的其他候补
        QueryWrapper<WaitlistRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seminar_room_id", mainWaitlist.getSeminarRoomId())
                .eq("wait_list_start_time", mainWaitlist.getWaitListStartTime())
                .eq("wait_list_end_time", mainWaitlist.getWaitListEndTime())
                .eq("wait_list_status", 0)  // 候补中
                .ne("id", mainWaitlist.getWaitListRecordId())  // 排除自己
                .orderByAsc("create_time")  // 按创建时间排序，先到先得
                .last("LIMIT 3");  // 最多再找3个，组成最多4人

        List<WaitlistRecord> otherWaitlists = waitlistRecordMapper.selectList(queryWrapper);
        groupWaitlists.addAll(otherWaitlists);

        return groupWaitlists;
    }

    /**
     * 创建预约记录列表
     */
    private List<ReservationRecord> createReservationRecords(List<WaitlistRecord> waitlists) {
        List<ReservationRecord> reservationRecords = new ArrayList<>();

        for (int i = 0; i < waitlists.size(); i++) {
            WaitlistRecord waitlist = waitlists.get(i);

            ReservationRecord record = new ReservationRecord();
            record.setStudentID(waitlist.getStudentId());
            record.setStudyRoomID(waitlist.getSeminarRoomId());
            record.setReservationStartTime(waitlist.getWaitListStartTime());
            record.setReservationEndTime(waitlist.getWaitListEndTime());

            // 设置研讨室人数（如果有）
            if (waitlist.getSeminarRoomNum() != null) {
                record.setSeminarRoomNum(waitlist.getSeminarRoomNum());
            }

            reservationRecords.add(record);
        }

        return reservationRecords;
    }

    /**
     * 更新组内所有候补记录状态
     */
    private void updateGroupWaitlistStatus(List<WaitlistRecord> waitlists) {
        for (WaitlistRecord waitlist : waitlists) {
            waitlist.setWaitListStatus(1);  // 已通过
            waitlistRecordMapper.updateById(waitlist);
        }
    }
}
