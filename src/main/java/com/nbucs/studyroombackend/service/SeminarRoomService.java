package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.SeminarRoom;

import java.util.List;

public interface SeminarRoomService {
    SeminarRoom addSeminarRoom(SeminarRoom seminarRoom);
    SeminarRoom updateSeminarRoom(SeminarRoom seminarRoom);
    boolean deleteSeminarRoom(SeminarRoom seminarRoom);
    List<SeminarRoom> getAllSeminarRooms();
    SeminarRoom getSeminarRoomById(String id);
}
