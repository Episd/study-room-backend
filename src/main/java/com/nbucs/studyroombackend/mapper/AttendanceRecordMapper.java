package com.nbucs.studyroombackend.mapper;

import com.nbucs.studyroombackend.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AttendanceRecordMapper {
    int insert(AttendanceRecord record);

    int updateById(AttendanceRecord record);

    AttendanceRecord selectById(@Param("id") String id);

    /**
     * 查询某个学生当天最近一次考勤记录（用于判断是否已签到但未签退）
     */
    AttendanceRecord selectLatestByStudentAndDay(
            @Param("studentId") int studentId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    /**
     * 查询学生所有考勤记录（可用于前端“我的考勤”）
     */
    List<AttendanceRecord> listByStudent(@Param("studentId") Integer studentId);
}

