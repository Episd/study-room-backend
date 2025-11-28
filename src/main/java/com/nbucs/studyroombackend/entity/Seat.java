package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("seat")
public class Seat {
    @TableId(value = "seatID", type = IdType.INPUT)
    private String seatId;
    private String seatLocation;
    private Integer seatType;
    private Integer seatStatus;
    private String seatBelonging;
}