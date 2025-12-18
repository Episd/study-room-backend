package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seatManage")
public class SeatManageController {
    @Autowired
    private SeatService seatService;


/**
 * 获取所有座位信息的接口方法
 * 使用GET方式请求/seats路径
 *
 * @return 返回一个Response对象，包含状态信息、消息和所有座位列表数据
 */
    @GetMapping("/seats")
    public Response<List<Seat>> getAllSeats() {
    // 调用studyRoomService中的getAllSeats方法获取所有座位信息
    // 并将结果封装到Response对象中返回给前端
        return Response.success("获取所有座位成功", seatService.getAllSeats());
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
}
