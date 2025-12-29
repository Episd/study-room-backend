package com.nbucs.studyroombackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nbucs.studyroombackend.entity.ReservationRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRecordMapper extends BaseMapper<ReservationRecord> {
    /**
     * 查询指定日期的所有预约ID
     */
    @Select("SELECT reservationRecordID FROM reservationrecord " +
            "WHERE reservationRecordID LIKE CONCAT('RR', #{date}, '%')")
    List<String> selectTodayReservationIds(@Param("date") String dateStr);

    /**
     * 查询今天最大的序号（使用SQL直接获取）
     */
    @Select("SELECT MAX(CAST(SUBSTRING(reservationRecordID, 11) AS UNSIGNED)) " +
            "FROM reservationrecord " +
            "WHERE reservationRecordID LIKE CONCAT('RR', #{date}, '%')")
    Integer selectTodayMaxSequence(@Param("date") String dateStr);

    /**
     * 查询学生当天最早的预约记录
     * @param studentId 学生ID
     * @return 最早的预约记录
     */
    @Select("SELECT * FROM reservationrecord " +
            "WHERE studentId = #{studentId} " +
            "AND DATE(reservationStartTime) = CURDATE() " +
            "AND reservationRecordStatus IN (1, 2) " +  // 1:已通过, 2:已开始
            "ORDER BY reservationStartTime ASC " +
            "LIMIT 1")
    ReservationRecord selectEarliestTodayReservation(@Param("studentId") String studentId);

    /**
     * 统计某个座位在指定时间区间内是否存在预约（区间重叠即算占用）
     * overlap 条件：已有.start <= queryEnd && 已有.end >= queryStart
     */
    @Select("""
    SELECT COUNT(1)
    FROM reservationrecord
    WHERE seatID = #{seatId}
      AND studyRoomID = #{studyRoomId}
      AND reservationRecordStatus IN (0, 1, 2)
      AND reservationStartTime <= #{endTime}
      AND reservationEndTime >= #{startTime}
""")
    Integer countOverlap(
            @Param("seatId") Long seatId,
            @Param("studyRoomId") Long studyRoomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


}