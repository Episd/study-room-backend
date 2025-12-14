package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.WaitlistRecord;

import java.util.List;

public interface WaitlistService {
    // TODO: 候补记录相关方法

    /**
     *  候补座位，添加候补记录
     */
    WaitlistRecord waitSeat(ReservationRecord reservationRecord);
    /**
     * 候补研讨室
     */
    WaitlistRecord waitSeminarRoom(ReservationRecord reservationRecord);
    /**
     * 查询指定学生的候补记录
     */
    List<WaitlistRecord> checkWaitlist(Integer studentId);
    /**
     * 取消候补记录
     */
    boolean cancelWaitlist(String reservationId);
    /**
     * 候补确认
     */
    boolean confirmWaitlist(String reservationId);
}
