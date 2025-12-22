package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("adminuser")
public class AdminUser {
    @TableId(value = "adminID", type = IdType.INPUT)
    private Integer adminID;
    @TableField("adminPassword")
    private String adminPassword;
    @TableField("adminPosition")
    private String adminPosition;
    @TableField("adminPermission")
    private Integer adminPermission;
    @TableField("adminPhoneNumber")
    private String adminPhoneNumber;
    @TableField("adminName")
    private String adminName;
}
