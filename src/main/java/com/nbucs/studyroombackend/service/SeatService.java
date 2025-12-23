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
    Seat updateSeat(Seat seat);

    List<Seat> updateSeats(Long roomID, List<Seat> list);

    /**
     * 删除座位
     */
    boolean deleteSeat(Seat seat);

    /**
     * 根据ID删除座位
     */
    boolean deleteSeat(Long seatId);

    /**
     * 获取所有座位
     */
    List<Seat> getAllSeats();

    /**
     * 根据ID获取座位
     */
    Seat getSeatById(Long seatId);

    /**
     * 根据自习室ID获取座位
     */
    List<Seat> getSeatsByStudyRoom(Long studyRoomId);

    /**
     * 根据座位状态获取座位
     */
    List<Seat> getSeatsByStatus(Integer status);

    /**
     * 根据座位类型获取座位
     */
    List<Seat> getSeatsByType(Integer type);

    /**
     * 更新座位预约状态
     */
    boolean updateSeatStatus(Long seatId, Integer status);

    boolean updateSeatCheckInStatus(Long seatId, Integer status);

    /**
     * 检查座位是否可用（可预约）
     */
    boolean isSeatAvailable(Long seatId);

    /**
     * 根据位置模糊查询座位
     */
    List<Seat> searchSeatsByLocation(String locationKeyword);

}
