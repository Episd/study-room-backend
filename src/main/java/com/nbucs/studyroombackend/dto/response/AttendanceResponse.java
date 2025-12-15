package com.nbucs.studyroombackend.dto.response;

import com.nbucs.studyroombackend.entity.AttendanceRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceResponse {
    private Long id;
    private Integer studentId;
    private Long roomId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public AttendanceResponse() {
        ;
    }
    public AttendanceResponse(Long id, LocalDateTime checkInTime) {
        this.id = id;
        this.checkInTime = checkInTime;
    }
    /**
     * 工厂方法：从 AttendanceRecord 转换为 AttendanceResponse
     */
    public static AttendanceResponse fromRecord(AttendanceRecord record) {
        if (record == null) {
            return null;
        }
        AttendanceResponse response = new AttendanceResponse();
        response.setId(record.getAttendanceRecordId());
        response.setStudentId(record.getStudentId());
        response.setRoomId(record.getSeminarRoomId()); // 注意这里对应数据库字段
        response.setCheckInTime(record.getCheckInTime());
        response.setCheckOutTime(record.getCheckOutTime());
        return response;
    }
}
