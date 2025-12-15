package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("seat")
public class Seat {
    @TableId(value = "seatID", type = IdType.AUTO)
    private Long seatId;

    @TableField("seatLocation")
    private String seatLocation;

    @TableField("seatType")
    private Integer seatType;

    @TableField("seatStatus")
    private Integer seatStatus;

    @TableField("seatBelonging")
    private Long seatBelonging;

    @TableField("seatNumber")
    private Integer seatNumber;
}