package com.nbucs.studyroombackend.service;

import com.nbucs.studyroombackend.entity.ReservationRecord;

import java.util.List;

/**
 * 预约服务接口，定义了与预约相关的服务方法
 * 该接口提供了预约座位、预约研讨室、查询预约记录、取消预约和接受预约等功能
 */
public interface ReservationService {
    // TODO: 确定预约座位、预约研讨室方法传入的参数类型

    /**
     * 预约座位，增加预约记录
     * @param reservationRecord 传入预约记录
     * @return  返回记录
     */
    ReservationRecord reserveSeat(ReservationRecord reservationRecord);

    /**
     * 预约研讨室，添加预约记录
     * @param reservationRecord 传入预约记录
     * @return 返回记录
     */
    ReservationRecord reserveSeminarRoom(ReservationRecord reservationRecord);
    /**
     * 查询指定学生的预约记录
     * @param studentId 学生ID，用于查询该学生的所有预约记录
     * @return 返回预约记录列表，包含该学生的所有预约信息
     */
    List<ReservationRecord> checkReservationRecord(Integer studentId);
    /**
     * 取消指定的预约
     * @param reservationId 预约记录ID，用于标识需要取消的预约
     * @return 返回操作是否成功，true表示取消成功，false表示取消失败
     */
    boolean cancelReservation(String reservationId);
    /**
     * 接受指定的预约
     * @param reservationId 预约记录ID，用于标识需要接受的预约
     * @return 返回操作是否成功，true表示接受成功，false表示接受失败
     */
    boolean acceptReservation(String reservationId);

    /**
     * 检查时间段冲突
     * @param reservationRecord  预约记录
     * @return 返回操作是否成功，true表示有冲突，false表示没有冲突
     */
    boolean checkTimeConflict(ReservationRecord reservationRecord);
}
