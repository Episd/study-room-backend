package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.entity.StudyRoom;
import com.nbucs.studyroombackend.service.SeminarRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seminarRoomManage")
public class SeminarRoomManageController {
    @Autowired
    private SeminarRoomService seminarRoomService;

    @GetMapping("/seminarRooms")
    public Response<List<SeminarRoom>> getSeminarRooms() {
        List<SeminarRoom> list = seminarRoomService.getAllSeminarRooms();
        return Response.success("查询所有研讨室成功！", list);
    }

    @PostMapping("/addSeminarRoom")
    public Response<SeminarRoom> addStudyRoom(@RequestBody SeminarRoom seminarRoom) {
        return Response.success("添加自习室成功", seminarRoomService.addSeminarRoom(seminarRoom));
    }

    @DeleteMapping("/deleteSeminarRoom/{id}")
    public Response<String> deleteSeminarRoom(@PathVariable("id") Long id) {
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomID(id);
        seminarRoomService.deleteSeminarRoom(seminarRoom);
        return Response.success("删除研讨室成功", null);
    }
}
