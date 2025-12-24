package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbucs.studyroombackend.entity.StudyRoom;
import com.nbucs.studyroombackend.exception.ServiceException;
import com.nbucs.studyroombackend.mapper.StudyRoomMapper;
import com.nbucs.studyroombackend.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {
    @Autowired
    private StudyRoomMapper studyRoomMapper;

    @Override
    public List<StudyRoom> getAllRooms() {
        return studyRoomMapper.selectList(null);
    }
    @Override
    public List<StudyRoom> getRooms(StudyRoom room) {
        if (room == null) {
            return studyRoomMapper.selectList(null);
        }
        return studyRoomMapper.selectList(new QueryWrapper<>(room));
    }
    @Override
    public StudyRoom addRoom(StudyRoom room) {
        int rows = studyRoomMapper.insert(room);
        if(rows > 0) {
            return room;
        } else {
            throw new ServiceException(500, "添加自习室失败");
        }
    }

    @Override
    public StudyRoom updateRoom(StudyRoom room) {
        int rows = studyRoomMapper.updateById(room);
        if(rows <= 0) {
            throw new ServiceException(500, "更新自习室失败");
        }
        return room;
    }

    @Override
    public boolean deleteRoom(StudyRoom room) {
        return studyRoomMapper.deleteById(room.getStudyRoomID()) > 0;
    }
}
