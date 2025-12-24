package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.StudyRoom;
import com.nbucs.studyroombackend.service.StudyRoomService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyRoomManage")
public class StudyRoomManageController {
    @Autowired
    private StudyRoomService studyRoomService;

    @GetMapping("/rooms")
    public Response<List<StudyRoom>> getAllRooms() {
        return Response.success("获取所有自习室成功", studyRoomService.getAllRooms());
    }

    @PostMapping("/addStudyRoom")
    public Response<StudyRoom> addStudyRoom(@RequestBody StudyRoom studyRoom) {
        return Response.success("添加自习室成功", studyRoomService.addRoom(studyRoom));
    }

    @PutMapping("/updateStudyRoom/{id}")
    public Response<StudyRoom> updateStudyRoom(@PathVariable("id") Long id, @RequestBody StudyRoom room) {
        room.setStudyRoomID(id);
        return Response.success("更新自习室成功", studyRoomService.updateRoom(room));
    }

    @DeleteMapping("/deleteStudyRoom/{id}")
    public Response<?> deleteStudyRoom(@PathVariable("id") Long id) {
        StudyRoom room = new StudyRoom();
        room.setStudyRoomID(id);
        studyRoomService.deleteRoom(room);
        return Response.success("删除自习室成功", null);
    }
}
