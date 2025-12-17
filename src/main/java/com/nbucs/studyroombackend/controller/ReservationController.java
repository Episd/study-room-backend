package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.request.OccupiedTimeSlotQueryDto;
import com.nbucs.studyroombackend.dto.request.ReserveSeatFormDto;
import com.nbucs.studyroombackend.dto.response.ReservationInfo;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/reserveSeat")
    public Response<?> reserveSeat(@RequestBody ReserveSeatFormDto reserveSeatFormDto) {
        System.out.println("预约请求已到达--学生ID：" + reserveSeatFormDto.getStudentId() + "房间ID" + reserveSeatFormDto.getStudyRoomId() + "座位ID：" + reserveSeatFormDto.getSeatId());
        try {
            ReservationRecord reservationRecord = new ReservationRecord();
            reservationRecord.setStudentID(reserveSeatFormDto.getStudentId());
            reservationRecord.setStudyRoomID(reserveSeatFormDto.getStudyRoomId());
            reservationRecord.setSeatID(reserveSeatFormDto.getSeatId());
            reservationRecord.setReservationStartTime(reserveSeatFormDto.getStartTime());
            reservationRecord.setReservationEndTime(reserveSeatFormDto.getEndTime());
            reservationRecord.setReservationRecordStatus(0);
            reservationRecord.setCancelPermission(1);
            reservationRecord.setCreateTime(LocalDateTime.now());
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

    @GetMapping("/getReservation")
    public Response<?> getStudentReservations(@RequestParam Integer studentId) {
        System.out.println("查询预约请求：学生ID：" + studentId);
        try {
            List<ReservationRecord> records = reservationService.checkReservationRecord(studentId);

            List<ReservationInfo> dtoList = records.stream().map(record -> {
                ReservationInfo info = new ReservationInfo();
                info.transformToDto(record);
                return info;
            }).collect(Collectors.toList());

            return Response.success("查询成功", dtoList);
        } catch (Exception e) {
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/earliest-today")
    public Response<?> getEarliestTodayReservation(@RequestParam String studentId) {
        System.out.println("查询当天最早预约请求：学生ID：" + studentId);
        try {
            ReservationRecord record = reservationService.getEarliestTodayReservation(studentId);

            if (record == null) {
                return Response.success("当天没有预约记录", null);
            }

            return Response.success("查询成功", record);
        } catch (Exception e) {
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }

    @PostMapping("/cancelReservation")
    public Response<?> cancelReservation(@RequestParam String reservationId) {
        System.out.println("取消预约请求：预约ID：" + reservationId);
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

    @PostMapping("/occupied-time-slots")
    public Response<?> getOccupiedTimeSlots(@RequestBody OccupiedTimeSlotQueryDto queryDto) {
        System.out.println("查询占用时间段：" + queryDto);

        try {
            // 基本参数验证
            if (queryDto.getQueryDate() == null) {
                return Response.error(400, "查询日期不能为空");
            }

            // 调用Service查询
            List<ReservationRecord> records = reservationService.getOccupiedTimeSlots(queryDto);

            if (records == null || records.isEmpty()) {
                return Response.success("该日期没有占用时间段", Collections.emptyList());
            }

            return Response.success("查询成功", records);

        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            System.err.println("查询失败: " + e.getMessage());
            return Response.error(500, "查询失败");
        }
    }

}