package com.nbucs.studyroombackend.servicetest;

import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeatServiceTest {
    @Autowired
    private SeatService seatService;

    @Test
    void testUpdateSeatStatus() {
        Seat seat = new Seat();
        seat.setSeatId("A001");
        seat.setSeatStatus(1);

        boolean result = seatService.updateSeatStatus(seat);
        System.out.println("Update result = " + result);
        System.out.println("正在测试更新座位状态");
    }

    @Test
    void testAddSeat() {
        ;
    }

    @Test
    void testDeleteSeat() {
        ;
    }

    @Test
    void testCheckSeat() {
        ;
    }
}
