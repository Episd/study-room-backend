package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.Response;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/seat")
    public Response<?> reserveSeat(@RequestBody ReservationRecord reservationRecord) {
        try {
            ReservationRecord record = reservationService.reserveSeat(reservationRecord);
            return Response.success("座位预约成功", record);
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    @PostMapping("/seminar-room")
    public Response<?> reserveSeminarRoom(@RequestBody ReservationRecord reservationRecord) {
        try {
            ReservationRecord record = reservationService.reserveSeminarRoom(reservationRecord);
            return Response.success("研讨室预约成功", record);
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public Response<?> getStudentReservations(@PathVariable Integer studentId) {
        try {
            List<ReservationRecord> records = reservationService.checkReservationRecord(studentId);
            return Response.success("查询成功", records);
        } catch (Exception e) {
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/cancel/{reservationId}")
    public Response<?> cancelReservation(@PathVariable String reservationId) {
        try {
            boolean result = reservationService.cancelReservation(reservationId);
            return result ? Response.success("取消成功", null) : Response.error(304, "取消失败");
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    @PutMapping("/accept/{reservationId}")
    public Response<?> acceptReservation(@PathVariable String reservationId) {
        try {
            boolean result = reservationService.acceptReservation(reservationId);
            return result ? Response.success("审核通过", null) : Response.error(304, "审核失败");
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }
}