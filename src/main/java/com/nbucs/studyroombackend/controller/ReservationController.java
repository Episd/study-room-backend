package com.nbucs.studyroombackend.controller;

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
    public Result reserveSeat(@RequestBody ReservationRecord reservationRecord) {
        try {
            ReservationRecord record = reservationService.reserveSeat(reservationRecord);
            return Result.success("座位预约成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/seminar-room")
    public Result reserveSeminarRoom(@RequestBody ReservationRecord reservationRecord) {
        try {
            ReservationRecord record = reservationService.reserveSeminarRoom(reservationRecord);
            return Result.success("研讨室预约成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public Result getStudentReservations(@PathVariable Integer studentId) {
        try {
            List<ReservationRecord> records = reservationService.checkReservationRecord(studentId);
            return Result.success("查询成功", records);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @PutMapping("/cancel/{reservationId}")
    public Result cancelReservation(@PathVariable String reservationId) {
        try {
            boolean result = reservationService.cancelReservation(reservationId);
            return result ? Result.success("取消成功") : Result.error("取消失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/accept/{reservationId}")
    public Result acceptReservation(@PathVariable String reservationId) {
        try {
            boolean result = reservationService.acceptReservation(reservationId);
            return result ? Result.success("审核通过") : Result.error("审核失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 统一的返回结果类
    public static class Result {
        private boolean success;
        private String message;
        private Object data;

        public Result() {}

        public static Result success(String message, Object data) {
            Result result = new Result();
            result.setSuccess(true);
            result.setMessage(message);
            result.setData(data);
            return result;
        }

        public static Result success(Object data) {
            return success("操作成功", data);
        }

        public static Result success(String message) {
            return success(message, null);
        }

        public static Result error(String message) {
            Result result = new Result();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }

        // Getter 和 Setter
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}