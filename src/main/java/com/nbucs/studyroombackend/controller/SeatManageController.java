package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seatManage")
public class SeatManageController {
    @Autowired
    private SeatService seatService;

    @GetMapping("/seats/{roomID}")
    public Response<List<Seat>> getSeatByRoomID(@PathVariable Long roomID) {
        return Response.success("获取座位成功", seatService.getSeatsByStudyRoom(roomID));
    }

    @GetMapping("/getSeatByRoomID")
    public Response<List<Seat>> getSeatByRoomID(@RequestParam Long roomID) {
        return Response.success("获取座位成功", seatService.getSeatsByStudyRoom(roomID));
    }

    // 获取指定座位的状态
    @GetMapping("/seat")
    public Response<Seat> getSeatById(@RequestParam Long seatID) {
        // 调用服务层查询座位
        Seat seat = seatService.getSeatById(seatID);
        return Response.success("获取座位成功", seat);
    }

    @PutMapping("/updateSeat/{seatID}")
    public Response<Seat> updateSeat(@PathVariable Long seatID, @RequestBody Map<String, String> body) {
        Seat seat = new Seat();
        seat.setSeatID(seatID);
        return Response.success("更新座位成功", seatService.updateSeat(seat));
    }

    @PutMapping("/saveSeats/{roomID}")
    public Response<?> saveSeats(@PathVariable Long roomID, @RequestBody List<Seat> seats) {
        return Response.success("保存座位成功", seatService.updateSeats(roomID, seats));
    }
}
