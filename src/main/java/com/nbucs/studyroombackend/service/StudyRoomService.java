package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.StudyRoom;

import java.util.List;

public interface StudyRoomService {
    // 查询所有自习室
    List<StudyRoom> getAllRooms();
    List<StudyRoom> getRooms(StudyRoom room);
    // 添加自习室
    StudyRoom addRoom(StudyRoom room);
    // 修改自习室信息
    StudyRoom updateRoom(StudyRoom room);
    // 删除自习室
    boolean deleteRoom(StudyRoom room);
}
