package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seatManage")
public class SeatManageController {
    @Autowired
    private SeatService studyRoomService;

    @GetMapping("/seats")
    public Response<List<Seat>> getAllSeats() {
        return Response.success("获取所有座位成功", studyRoomService.getAllSeats());
    }
}
