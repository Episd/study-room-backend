package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.StudyRoom;
import com.nbucs.studyroombackend.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resourceManage")
public class StudyRoomManageController {
    @Autowired
    private StudyRoomService studyRoomService;

    @GetMapping("/rooms")
    public Response<List<StudyRoom>> getAllRooms() {
        return Response.success("获取所有自习室成功", studyRoomService.getAllRooms());
    }
}
