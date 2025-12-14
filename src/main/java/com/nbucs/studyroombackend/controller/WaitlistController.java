package com.nbucs.studyroombackend.controller;

import com.nbucs.studyroombackend.dto.request.ReserveSeatFormDto;
import com.nbucs.studyroombackend.dto.response.Response;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.WaitlistRecord;
import com.nbucs.studyroombackend.service.WaitlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wait")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    /**
     * 候补座位
     */
    @PostMapping("/waitSeat")
    public Response<?> waitSeat(@RequestBody ReserveSeatFormDto reserveSeatFormDto) {
        System.out.println("候补座位请求已到达--学生ID：" + reserveSeatFormDto.getStudentId() +
                " 房间ID：" + reserveSeatFormDto.getStudyRoomId() +
                " 座位ID：" + reserveSeatFormDto.getSeatId());
        try {
            ReservationRecord reservationRecord = new ReservationRecord();
            reservationRecord.setStudentId(reserveSeatFormDto.getStudentId());
            reservationRecord.setStudyRoomId(reserveSeatFormDto.getStudyRoomId());
            reservationRecord.setSeatId(reserveSeatFormDto.getSeatId());
            reservationRecord.setReservationStartTime(reserveSeatFormDto.getStartTime());
            reservationRecord.setReservationEndTime(reserveSeatFormDto.getEndTime());
            reservationRecord.setReservationRecordStatus(0);
            reservationRecord.setCancelPermission(1);
            reservationRecord.setCreateTime(LocalDateTime.now());

            WaitlistRecord record = waitlistService.waitSeat(reservationRecord);
            return Response.success("座位候补成功", record);
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    /**
     * 候补研讨室
     */
    @PostMapping("/waitSeminarRoom")
    public Response<?> waitSeminarRoom(@RequestBody ReservationRecord reservationRecord) {
        try {
            WaitlistRecord record = waitlistService.waitSeminarRoom(reservationRecord);
            return Response.success("研讨室候补成功", record);
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    /**
     * 查询学生的候补记录
     */
    @GetMapping("/getWaitlist")
    public Response<?> getStudentWaitlist(@RequestParam Integer studentId) {
        System.out.println("查询候补记录请求：学生ID：" + studentId);
        try {
            List<WaitlistRecord> records = waitlistService.checkWaitlist(studentId);
            return Response.success("查询成功", records);
        } catch (Exception e) {
            return Response.error(304, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 取消候补
     */
    @PostMapping("/cancelWaitlist")
    public Response<?> cancelWaitlist(@RequestParam String waitlistRecordId) {
        System.out.println("取消候补请求：候补记录ID：" + waitlistRecordId);
        try {
            boolean result = waitlistService.cancelWaitlist(waitlistRecordId);
            return result ? Response.success("候补取消成功", null) : Response.error(304, "候补取消失败");
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }

    /**
     * 确认候补（将候补转为正式预约）
     */
    @PostMapping("/confirmWaitlist")
    public Response<?> confirmWaitlist(@RequestParam String waitlistRecordId) {
        System.out.println("确认候补请求：候补记录ID：" + waitlistRecordId);
        try {
            boolean result = waitlistService.confirmWaitlist(waitlistRecordId);
            return result ? Response.success("候补确认成功，已转为正式预约", null) : Response.error(304, "候补确认失败");
        } catch (Exception e) {
            return Response.error(304, e.getMessage());
        }
    }
}