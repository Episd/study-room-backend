package com.nbucs.studyroombackend.servicetest;

import com.nbucs.studyroombackend.entity.ReservationRecord;
import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.service.ReservationService;
import com.nbucs.studyroombackend.service.SeatService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SeatService 测试类
 * 作用：测试座位服务的各个功能，确保业务逻辑正确
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 按顺序执行测试
@Transactional // 测试完成后回滚事务，不污染数据库
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    private static final String RESERVATION_ID = "RES002";
    private static final Integer STUDENT_ID = 20230002;
    private static final String STUDY_ROOM_ID = "A101";
    private static final String SEAT_ID = "A101-04";
    private static final String SEMINAR_ID = "S001";


    @Test
    @Order(1)
    public void TestReserveSeat() {
        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setReservationId(RESERVATION_ID);
        reservationRecord.setStudentId(STUDENT_ID);
        reservationRecord.setStudyRoomId(STUDY_ROOM_ID);
        reservationRecord.setSeatId(SEAT_ID);
        reservationRecord.setReservationStartTime(LocalDateTime.of(2025,11,23,9,30));
        reservationRecord.setReservationEndTime(LocalDateTime.of(2025,11,23,11,30));

        try{
            ReservationRecord result = reservationService.reserveSeat(reservationRecord);

            System.out.println(result);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    public void TestReserveSeminarRoom () {
        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setReservationId(RESERVATION_ID);
        reservationRecord.setStudentId(STUDENT_ID);
        reservationRecord.setSeminarRoomId(SEMINAR_ID);
        reservationRecord.setSeminarRoomNum(4);
        reservationRecord.setReservationStartTime(LocalDateTime.of(2025,11,23,9,30));
        reservationRecord.setReservationEndTime(LocalDateTime.of(2025,11,23,11,30));

        try {
            ReservationRecord result = reservationService.reserveSeminarRoom(reservationRecord);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    public void testGetAllReservations() {
        System.out.println("=== 测试获取指定学生预约记录 ===");

        try {
            List<ReservationRecord> result = reservationService.checkReservationRecord(STUDENT_ID);

            System.out.println(result);
            if (result.size() > 0) {
                System.out.println("前3个：");
                result.stream().limit(3).forEach(ReservationRecord ->
                        System.out.println("  - " + ReservationRecord.getReservationId()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void TestCancelReservation() {

        try{
            boolean result = reservationService.cancelReservation(RESERVATION_ID);
            System.out.println(result);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    public void TestAcceptReservation() {
        try{
            boolean result = reservationService.acceptReservation(RESERVATION_ID);
            System.out.println(result);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    public void TestCheckTimeConflict() {

        ReservationRecord reservationRecord = new ReservationRecord();
        reservationRecord.setReservationId(RESERVATION_ID);
        reservationRecord.setStudentId(20230003);
        reservationRecord.setSeminarRoomId("S002");
        reservationRecord.setSeminarRoomNum(4);
        reservationRecord.setReservationStartTime(LocalDateTime.of(2024,1,1,9,30));
        reservationRecord.setReservationEndTime(LocalDateTime.of(2024,1,1,11,30));

        try{
            boolean result = reservationService.checkTimeConflict(reservationRecord);
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
