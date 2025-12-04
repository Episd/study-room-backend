package com.nbucs.studyroombackend.servicetest;



import com.nbucs.studyroombackend.entity.SeminarRoom;
import com.nbucs.studyroombackend.service.SeminarRoomService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

/**
 * SeatService 测试类
 * 作用：测试座位服务的各个功能，确保业务逻辑正确
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 按顺序执行测试
@Transactional // 测试完成后回滚事务，不污染数据库
public class SeminarRoomServiceTest {
    @Autowired
    private SeminarRoomService seminarRoomService;

    private static final String TEST_SEMINAR_ROOM = "S006";

    @Test
    @Order(1)
    public void testAddSeminarRoom() {
        System.out.println("=== 测试添加研讨室 ===");

        // 准备测试数据
        SeminarRoom seminarRoom = new SeminarRoom();
        seminarRoom.setSeminarRoomId(TEST_SEMINAR_ROOM);
        seminarRoom.setSeminarRoomLocation("{\"building\": \"C栋\", \"floor\": 3, \"room\": \"106\"}");
        seminarRoom.setSeminarRoomMin(3);
        seminarRoom.setSeminarRoomMax(10);
        seminarRoom.setSeminarRoomStatus(0);
        seminarRoom.setCurrentNum(0);
        seminarRoom.setSeminarRoomOpentime(LocalTime.of(8, 0));


        try {
            boolean result = seminarRoomService.addSeminarRoom(seminarRoom);

            assertTrue(result, "添加研讨室应该成功");
            System.out.println("✅ 添加研讨室测试通过");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("添加研讨室报错");
        }
    }

    @Test
    @Order(2)
    public void testUpdateSeminarRoom() {
        System.out.println("=== 测试更新座位 ===");

        SeminarRoom seminarRoom = seminarRoomService.getSeminarRoomById(TEST_SEMINAR_ROOM);
        if (seminarRoom == null) {
            // 如果座位不存在，先添加一个
            testAddSeminarRoom();
            seminarRoom = seminarRoomService.getSeminarRoomById(TEST_SEMINAR_ROOM);
        }

        seminarRoom.setCurrentNum(seminarRoom.getCurrentNum() + 1);
        seminarRoom.setSeminarRoomStatus(1);

        try {
            boolean result = seminarRoomService.updateSeminarRoom(seminarRoom);

            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testGetAllSeminarRoom() {
        System.out.println("=== 测试获取所有研讨室 ===");

        try {
            List<SeminarRoom> result = seminarRoomService.getAllSeminarRooms();

            System.out.println(result);
            if (result.size() > 0) {
                System.out.println("前3个：");
                result.stream().limit(3).forEach(seminarRoom ->
                        System.out.println("  - " + seminarRoom.getSeminarRoomId()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
