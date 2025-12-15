package com.nbucs.studyroombackend.dto.response;

import com.nbucs.studyroombackend.entity.ReservationRecord;
import lombok.Data;
import java.time.format.DateTimeFormatter;

@Data
public class ReservationInfo {
    private String id;
    private Long roomId;
    private Long seatId;
    private String date;
    private String timeSlot;
    private String status;

    public void transformToDto(ReservationRecord reservationRecord) {
        this.id = reservationRecord.getReservationRecordId();
        this.roomId = reservationRecord.getStudyRoomId();
        this.seatId = reservationRecord.getSeatId();
        // 日期格式化
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.date = reservationRecord.getReservationStartTime() != null
                ? reservationRecord.getReservationStartTime().format(dateFormatter)
                : null;

        // 时间段格式化（起止时间）
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        if (reservationRecord.getReservationStartTime() != null && reservationRecord.getReservationEndTime() != null) {
            this.timeSlot = reservationRecord.getReservationStartTime().format(timeFormatter)
                    + " - " + reservationRecord.getReservationEndTime().format(timeFormatter);
        } else {
            this.timeSlot = null;
        }

        if (reservationRecord.getReservationRecordStatus() != null) {
            switch (reservationRecord.getReservationRecordStatus()) {
                case 0:
                    this.status = "待审核";
                    break;
                case 1:
                    this.status = "已通过";
                    break;
                case 2:
                    this.status = "已开始";
                    break;
                case 3:
                    this.status = "已结束";
                    break;
                default:
                    this.status = "已取消";
            }
        } else {
            this.status = null;
        }
    }
}
