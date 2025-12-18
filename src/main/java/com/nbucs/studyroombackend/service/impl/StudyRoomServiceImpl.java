package com.nbucs.studyroombackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbucs.studyroombackend.entity.StudyRoom;
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
    public boolean addRoom(StudyRoom room) {
        return studyRoomMapper.insert(room) > 0;
    }

    @Override
    public boolean updateRoom(StudyRoom room) {
        return studyRoomMapper.updateById(room) > 0;
    }

    @Override
    public boolean deleteRoom(StudyRoom room) {
        return studyRoomMapper.deleteById(room.getStudyRoomID()) > 0;
    }
}
