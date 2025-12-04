package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 添加这行导入
import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.mapper.SeminarRoomMapper;
import com.nbucs.studyroombackend.service.SeminarRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class SeminarRoomServiceImpl implements SeminarRoomService {
    @Autowired
    private SeminarRoomMapper seminarRoomMapper;

    @Override
    public boolean addSeminarRoom(SeminarRoom seminarRoom){
        // 1. 参数验证
        if (seminarRoom == null) {
            throw new IllegalArgumentException("研讨室信息不能为空");
        }

        // 2. 验证必填字段
        if (seminarRoom.getSeminarRoomId() == null || seminarRoom.getSeminarRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("研讨室ID不能为空");
        }

        if (seminarRoom.getSeminarRoomLocation() == null || seminarRoom.getSeminarRoomLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("研讨室位置不能为空");
        }

        // 3. 验证人数范围（必填字段）
        if (seminarRoom.getSeminarRoomMin() == null) {
            throw new IllegalArgumentException("最低人数不能为空");
        }

        if (seminarRoom.getSeminarRoomMax() == null) {
            throw new IllegalArgumentException("最大人数不能为空");
        }

        // 验证人数合理性
        if (seminarRoom.getSeminarRoomMin() <= 0) {
            throw new IllegalArgumentException("最低人数必须大于0");
        }

        if (seminarRoom.getSeminarRoomMax() <= 0) {
            throw new IllegalArgumentException("最大人数必须大于0");
        }

        if (seminarRoom.getSeminarRoomMin() > seminarRoom.getSeminarRoomMax()) {
            throw new IllegalArgumentException("最低人数不能大于最大人数");
        }

        // 4. 检查是否已存在
        SeminarRoom existing = seminarRoomMapper.selectById(seminarRoom.getSeminarRoomId());
        if (existing != null) {
            throw new RuntimeException("研讨室ID已存在：" + seminarRoom.getSeminarRoomId());
        }

        // 5. 设置默认值
        if (seminarRoom.getSeminarRoomStatus() == null) {
            seminarRoom.setSeminarRoomStatus(0); // 0-空闲
        }

        if (seminarRoom.getCurrentNum() == null) {
            seminarRoom.setCurrentNum(0);
        }

        if (seminarRoom.getSeminarRoomOpentime() == null) {
            seminarRoom.setSeminarRoomOpentime(LocalTime.of(8, 0)); // 默认8:00开放
        }

        // 6. 插入数据库
        int result = seminarRoomMapper.insert(seminarRoom);
        return result > 0;
    }

    @Override
    public boolean updateSeminarRoom(SeminarRoom seminarRoom){
        // 1. 参数验证
        if (seminarRoom == null || seminarRoom.getSeminarRoomId() == null) {
            throw new IllegalArgumentException("研讨室ID不能为空");
        }

        // 2. 检查记录是否存在
        SeminarRoom existing = seminarRoomMapper.selectById(seminarRoom.getSeminarRoomId());
        if (existing == null) {
            throw new RuntimeException("研讨室不存在：" + seminarRoom.getSeminarRoomId());
        }

        // 3. 验证人数范围（如果提供了新值）
        if (seminarRoom.getSeminarRoomMin() != null) {
            if (seminarRoom.getSeminarRoomMin() <= 0) {
                throw new IllegalArgumentException("最低人数必须大于0");
            }
        }

        if (seminarRoom.getSeminarRoomMax() != null) {
            if (seminarRoom.getSeminarRoomMax() <= 0) {
                throw new IllegalArgumentException("最大人数必须大于0");
            }
        }

        // 如果两个人数都提供了，验证范围关系
        if (seminarRoom.getSeminarRoomMin() != null && seminarRoom.getSeminarRoomMax() != null) {
            if (seminarRoom.getSeminarRoomMin() > seminarRoom.getSeminarRoomMax()) {
                throw new IllegalArgumentException("最低人数不能大于最大人数");
            }
        }

        // 4. 使用MyBatis-Plus的自动填充功能更新非空字段
        // 这里updateById会只更新非null的字段
        int result = seminarRoomMapper.updateById(seminarRoom);
        return result > 0;
    }

    @Override
    public boolean deleteSeminarRoom(SeminarRoom seminarRoom){
        if (seminarRoom == null || seminarRoom.getSeminarRoomId() == null) {
            throw new IllegalArgumentException("研讨室ID不能为空");
        }
        return deleteSeminarRoom(seminarRoom.getSeminarRoomId());
    }

    public boolean deleteSeminarRoom(String seminarRoomId) {
        if (seminarRoomId == null || seminarRoomId.trim().isEmpty()) {
            throw new IllegalArgumentException("研讨室ID不能为空");
        }

        // 检查是否有关联的预约记录
        // 这里可以添加业务逻辑检查，比如有预约记录时不能删除

        int result = seminarRoomMapper.deleteById(seminarRoomId);
        return result > 0;
    }

    @Override
    public List<SeminarRoom> getAllSeminarRooms(){
        // 使用 LambdaQueryWrapper
        LambdaQueryWrapper<SeminarRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SeminarRoom::getSeminarRoomId);
        return seminarRoomMapper.selectList(queryWrapper);
    }

    public boolean canBeReserved(String seminarRoomId, int numberOfPeople) {
        SeminarRoom room = seminarRoomMapper.selectById(seminarRoomId);
        if (room == null) {
            return false;
        }

        // 检查状态是否空闲
        if (room.getSeminarRoomStatus() != 0) {
            return false;
        }

        // 检查人数是否符合要求
        return numberOfPeople >= room.getSeminarRoomMin() &&
                numberOfPeople <= room.getSeminarRoomMax();
    }

    // 新增：根据状态查询研讨室
    public List<SeminarRoom> getSeminarRoomsByStatus(Integer status) {
        LambdaQueryWrapper<SeminarRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeminarRoom::getSeminarRoomStatus, status)
                .orderByAsc(SeminarRoom::getSeminarRoomId);
        return seminarRoomMapper.selectList(queryWrapper);
    }

    // 新增：根据ID查询单个研讨室
    public SeminarRoom getSeminarRoomById(String seminarRoomId) {
        return seminarRoomMapper.selectById(seminarRoomId);
    }

    // 新增：根据位置模糊查询
    public List<SeminarRoom> searchSeminarRoomsByLocation(String locationKeyword) {
        LambdaQueryWrapper<SeminarRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SeminarRoom::getSeminarRoomLocation, locationKeyword)
                .orderByAsc(SeminarRoom::getSeminarRoomId);
        return seminarRoomMapper.selectList(queryWrapper);
    }

    // 新增：更新研讨室状态
    public boolean updateSeminarRoomStatus(String seminarRoomId, Integer status) {
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomId(seminarRoomId);
        seminarRoom.setSeminarRoomStatus(status);
        return updateSeminarRoom(seminarRoom);
    }

    // 新增：更新当前人数
    public boolean updateCurrentNum(String seminarRoomId, Integer currentNum) {
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomId(seminarRoomId);
        seminarRoom.setCurrentNum(currentNum);
        return updateSeminarRoom(seminarRoom);
    }
}