package com.nbucs.studyroombackend.service.impl;

import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.service.ReservationService;
import java.util.List;

public class ReservationServiceImpl implements ReservationService {
    // TODO: 实现预约服务的方法
    @Override
    public ReservationRecord reserveSeat() {
        return null;
    }

    @Override
    public ReservationRecord reserveSeminarRoom() {
        return null;
    }

    @Override
    public List<ReservationRecord> checkReservationRecord(String studentId) {
        return null;
    }

    @Override
    public boolean cancelReservation(String reservationRecordId) {
        return false;
    }

    @Override
    public boolean acceptReservation(String reservationRecordId) {
        return false;
    }
}
