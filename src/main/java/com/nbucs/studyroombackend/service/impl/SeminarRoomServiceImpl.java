package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 添加这行导入
import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.exception.ServiceException;
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
    public SeminarRoom addSeminarRoom(SeminarRoom seminarRoom){
        // 1. 参数验证
        if (seminarRoom == null) {
            throw new IllegalArgumentException("研讨室信息不能为空");
        }

        // 2. 验证位置（必填字段）
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
        if(result > 0) {
            return seminarRoom;
        } else {
            throw new ServiceException(500, "添加研讨室失败");
        }
    }

    @Override
    public SeminarRoom updateSeminarRoom(SeminarRoom seminarRoom){
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
        if(result <= 0) throw new ServiceException(500, "更新研讨室失败");
        return seminarRoom;
    }

    @Override
    public boolean deleteSeminarRoom(SeminarRoom seminarRoom){
        if (seminarRoom == null || seminarRoom.getSeminarRoomID() == null) {
            throw new IllegalArgumentException("研讨室ID不能为空");
        }
        return deleteSeminarRoom(seminarRoom.getSeminarRoomID());
    }

    public boolean deleteSeminarRoom(Long seminarRoomId) {
        if (seminarRoomId == null) {
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
        queryWrapper.orderByAsc(SeminarRoom::getSeminarRoomID);
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
                .orderByAsc(SeminarRoom::getSeminarRoomID);
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
                .orderByAsc(SeminarRoom::getSeminarRoomID);
        return seminarRoomMapper.selectList(queryWrapper);
    }

    // 新增：更新研讨室状态
    public boolean updateSeminarRoomStatus(Long seminarRoomId, Integer status) {
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomID(seminarRoomId);
        seminarRoom.setSeminarRoomStatus(status);
        updateSeminarRoom(seminarRoom);
        return true;
    }

    // 新增：更新当前人数
    public boolean updateCurrentNum(Long seminarRoomId, Integer currentNum) {
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomID(seminarRoomId);
        seminarRoom.setCurrentNum(currentNum);
        updateSeminarRoom(seminarRoom);
        return true;
    }
}