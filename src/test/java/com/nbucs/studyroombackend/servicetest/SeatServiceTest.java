package com.nbucs.studyroombackend.servicetest;

import com.nbucs.studyroombackend.entity.Seat;
import com.nbucs.studyroombackend.service.SeatService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SeatService 测试类
 * 作用：测试座位服务的各个功能，确保业务逻辑正确
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 按顺序执行测试
@Transactional // 测试完成后回滚事务，不污染数据库
public class SeatServiceTest {

    @Autowired
    private SeatService seatService;

    // 测试用的常量
    private static final String TEST_SEAT_ID = "A102-05";
    private static final String TEST_STUDY_ROOM_ID = "A102  ";

    @Test
    @Order(1)
    void testAddSeat() {
        System.out.println("=== 测试添加座位 ===");

        // 准备测试数据
        Seat seat = new Seat();
        seat.setSeatId(TEST_SEAT_ID);
        seat.setSeatLocation("{\"row\": 3, \"col\": 9}");
        seat.setSeatBelonging(TEST_STUDY_ROOM_ID);
        seat.setSeatType(0); // 通用座位
        seat.setSeatStatus(0); // 可预约

        try {
            // 执行测试
            boolean result = seatService.addSeat(seat);

            // 验证结果
            assertTrue(result, "添加座位应该成功");
            System.out.println("✅ 添加座位测试通过");

            // 验证座位确实被添加
            Seat savedSeat = seatService.getSeatById(TEST_SEAT_ID);
            assertNotNull(savedSeat, "添加后应该能查询到座位");
            assertEquals("{\"row\": 3, \"col\": 9}", savedSeat.getSeatLocation());
            assertEquals(TEST_STUDY_ROOM_ID, savedSeat.getSeatBelonging());

        } catch (Exception e) {
            System.err.println("❌ 添加座位测试失败: " + e.getMessage());
            fail("添加座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    void testUpdateSeat() {
        System.out.println("=== 测试更新座位 ===");

        // 先确保座位存在（依赖 testAddSeat）
        Seat seat = seatService.getSeatById(TEST_SEAT_ID);
        if (seat == null) {
            // 如果座位不存在，先添加一个
            testAddSeat();
            seat = seatService.getSeatById(TEST_SEAT_ID);
        }

        // 修改座位信息
        seat.setSeatLocation("A区第1排第1列(已更新)");
        seat.setSeatStatus(1); // 更新为已预约

        try {
            // 执行更新
            boolean result = seatService.updateSeat(seat);

            // 验证结果
            assertTrue(result, "更新座位应该成功");
            System.out.println("✅ 更新座位测试通过");

            // 验证更新后的数据
            Seat updatedSeat = seatService.getSeatById(TEST_SEAT_ID);
            assertNotNull(updatedSeat);
            assertEquals("A区第1排第1列(已更新)", updatedSeat.getSeatLocation());
            assertEquals(1, updatedSeat.getSeatStatus());

        } catch (Exception e) {
            System.err.println("❌ 更新座位测试失败: " + e.getMessage());
            fail("更新座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testUpdateSeatStatus() {
        System.out.println("=== 测试更新座位状态 ===");

        try {
            // 方法1：使用单独的更新状态方法
            boolean result = seatService.updateSeatStatus(TEST_SEAT_ID, 2); // 更新为未签到

            assertTrue(result, "更新座位状态应该成功");
            System.out.println("✅ 更新座位状态测试通过");

            // 验证状态已更新
            Seat seat = seatService.getSeatById(TEST_SEAT_ID);
            assertNotNull(seat);
            assertEquals(2, seat.getSeatStatus(), "座位状态应该更新为2(未签到)");

        } catch (Exception e) {
            System.err.println("❌ 更新座位状态测试失败: " + e.getMessage());
            fail("更新座位状态不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testCheckSeatAvailability() {
        System.out.println("=== 测试检查座位可用性 ===");

        try {
            // 测试座位是否可用（当前状态为2-未签到，应该不可用）
            boolean isAvailable = seatService.isSeatAvailable(TEST_SEAT_ID);

            assertFalse(isAvailable, "状态为2的座位应该不可用");
            System.out.println("✅ 检查座位可用性测试通过");

            // 测试不存在的座位
            boolean nonExistent = seatService.isSeatAvailable("NON_EXISTENT_SEAT");
            assertFalse(nonExistent, "不存在的座位应该返回false");

        } catch (Exception e) {
            System.err.println("❌ 检查座位可用性测试失败: " + e.getMessage());
            fail("检查座位可用性不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    void testGetSeatsByStudyRoom() {
        System.out.println("=== 测试按自习室查询座位 ===");

        try {
            List<Seat> seats = seatService.getSeatsByStudyRoom(TEST_STUDY_ROOM_ID);

            assertNotNull(seats, "返回的座位列表不应该为null");
            assertFalse(seats.isEmpty(), "应该至少有一个座位");
            System.out.println("✅ 按自习室查询座位测试通过，找到 " + seats.size() + " 个座位");

            // 验证查询结果
            boolean foundTestSeat = seats.stream()
                    .anyMatch(seat -> TEST_SEAT_ID.equals(seat.getSeatId()));
            assertTrue(foundTestSeat, "应该包含测试座位");

        } catch (Exception e) {
            System.err.println("❌ 按自习室查询座位测试失败: " + e.getMessage());
            fail("按自习室查询座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testGetAllSeats() {
        System.out.println("=== 测试获取所有座位 ===");

        try {
            List<Seat> allSeats = seatService.getAllSeats();

            assertNotNull(allSeats, "返回的所有座位列表不应该为null");
            System.out.println("✅ 获取所有座位测试通过，共 " + allSeats.size() + " 个座位");

            // 如果有数据，打印前几个
            if (!allSeats.isEmpty()) {
                System.out.println("前3个座位：");
                allSeats.stream().limit(3).forEach(seat ->
                        System.out.println("  - " + seat.getSeatId() + ": " + seat.getSeatLocation())
                );
            }

        } catch (Exception e) {
            System.err.println("❌ 获取所有座位测试失败: " + e.getMessage());
            fail("获取所有座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testDeleteSeat() {
        System.out.println("=== 测试删除座位 ===");

        try {
            // 先修改座位状态为可删除状态（0-可预约或5-维修中）
            seatService.updateSeatStatus(TEST_SEAT_ID, 0);

            // 执行删除
            boolean result = seatService.deleteSeat(TEST_SEAT_ID);

            assertTrue(result, "删除座位应该成功");
            System.out.println("✅ 删除座位测试通过");

            // 验证座位已被删除
            Seat deletedSeat = seatService.getSeatById(TEST_SEAT_ID);
            assertNull(deletedSeat, "删除后不应该能查询到座位");

        } catch (Exception e) {
            System.err.println("❌ 删除座位测试失败: " + e.getMessage());
            fail("删除座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testInvalidOperations() {
        System.out.println("=== 测试非法操作 ===");

        // 测试1：添加空座位应该失败
        assertThrows(IllegalArgumentException.class, () -> {
            seatService.addSeat(null);
        }, "添加空座位应该抛出IllegalArgumentException");

        // 测试2：添加没有ID的座位应该失败
        assertThrows(IllegalArgumentException.class, () -> {
            Seat seat = new Seat();
            seat.setSeatLocation("测试位置");
            seat.setSeatBelonging("测试自习室");
            seatService.addSeat(seat);
        }, "添加没有ID的座位应该抛出IllegalArgumentException");

        // 测试3：删除不存在的座位
        boolean result = seatService.deleteSeat("NON_EXISTENT_SEAT_999");
        assertFalse(result, "删除不存在的座位应该返回false");

        System.out.println("✅ 非法操作测试通过");
    }

    @Test
    @Order(9)
    void testGetSeatsByStatus() {
        System.out.println("=== 测试按状态查询座位 ===");

        try {
            // 查询所有可预约的座位
            List<Seat> availableSeats = seatService.getSeatsByStatus(0);

            assertNotNull(availableSeats, "返回的座位列表不应该为null");
            System.out.println("✅ 按状态查询座位测试通过，找到 " + availableSeats.size() + " 个可预约座位");

            // 如果有数据，验证状态
            availableSeats.forEach(seat ->
                    assertEquals(0, seat.getSeatStatus(), "所有座位状态都应该是0(可预约)")
            );

        } catch (Exception e) {
            System.err.println("❌ 按状态查询座位测试失败: " + e.getMessage());
            fail("按状态查询座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    void testSearchSeatsByLocation() {
        System.out.println("=== 测试按位置搜索座位 ===");

        try {
            // 搜索包含"A区"的座位
            List<Seat> seats = seatService.searchSeatsByLocation("A区");

            assertNotNull(seats, "返回的座位列表不应该为null");
            System.out.println("✅ 按位置搜索座位测试通过，找到 " + seats.size() + " 个座位");

            // 验证搜索结果都包含关键词
            seats.forEach(seat ->
                    assertTrue(seat.getSeatLocation().contains("A区"),
                            "座位位置应该包含'A区'")
            );

        } catch (Exception e) {
            System.err.println("❌ 按位置搜索座位测试失败: " + e.getMessage());
            fail("按位置搜索座位不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    void testBatchOperations() {
        System.out.println("=== 测试批量操作 ===");

        try {
            // 假设 seatService 有批量操作方法
            if (seatService instanceof com.nbucs.studyroombackend.service.impl.SeatServiceImpl) {
                com.nbucs.studyroombackend.service.impl.SeatServiceImpl impl =
                        (com.nbucs.studyroombackend.service.impl.SeatServiceImpl) seatService;

                // 测试批量更新状态
                boolean batchResult = impl.batchUpdateSeatStatus(
                        List.of("A101-01", "S002", "S003"), 0);

                System.out.println("✅ 批量操作测试完成，结果: " + batchResult);
            } else {
                System.out.println("⚠️ 跳过批量操作测试（服务实现不支持）");
            }

        } catch (Exception e) {
            System.err.println("❌ 批量操作测试失败: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    void testGetTypeAndStatusDescription() {
        System.out.println("=== 测试获取类型和状态描述 ===");

        try {
            if (seatService instanceof com.nbucs.studyroombackend.service.impl.SeatServiceImpl) {
                com.nbucs.studyroombackend.service.impl.SeatServiceImpl impl =
                        (com.nbucs.studyroombackend.service.impl.SeatServiceImpl) seatService;

                // 测试状态描述
                String statusDesc = impl.getStatusDescription(0);
                assertEquals("可预约", statusDesc, "状态0的描述应该是'可预约'");

                // 测试类型描述
                String typeDesc = impl.getTypeDescription(0);
                assertEquals("通用座位", typeDesc, "类型0的描述应该是'通用座位'");

                System.out.println("✅ 获取描述测试通过");
                System.out.println("  - 状态0: " + statusDesc);
                System.out.println("  - 类型0: " + typeDesc);
            } else {
                System.out.println("⚠️ 跳过描述测试（服务实现不支持）");
            }

        } catch (Exception e) {
            System.err.println("❌ 获取描述测试失败: " + e.getMessage());
        }
    }
}
