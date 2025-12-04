package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.Seat;

import java.util.List;

public interface SeatService {
    /**
     * 添加座位
     */
    boolean addSeat(Seat seat);

    /**
     * 更新座位信息
     */
    boolean updateSeat(Seat seat);

    /**
     * 删除座位
     */
    boolean deleteSeat(Seat seat);

    /**
     * 根据ID删除座位
     */
    boolean deleteSeat(String seatId);

    /**
     * 获取所有座位
     */
    List<Seat> getAllSeats();

    /**
     * 根据ID获取座位
     */
    Seat getSeatById(String seatId);

    /**
     * 根据自习室ID获取座位
     */
    List<Seat> getSeatsByStudyRoom(String studyRoomId);

    /**
     * 根据座位状态获取座位
     */
    List<Seat> getSeatsByStatus(Integer status);

    /**
     * 根据座位类型获取座位
     */
    List<Seat> getSeatsByType(Integer type);

    /**
     * 更新座位状态
     */
    boolean updateSeatStatus(String seatId, Integer status);

    /**
     * 检查座位是否可用（可预约）
     */
    boolean isSeatAvailable(String seatId);

    /**
     * 根据位置模糊查询座位
     */
    List<Seat> searchSeatsByLocation(String locationKeyword);

}
