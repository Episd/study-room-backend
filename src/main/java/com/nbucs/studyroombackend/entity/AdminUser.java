package com.nbucs.studyroombackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("adminuser")
public class AdminUser {
    @TableId(value = "adminID", type = IdType.INPUT)
    private Integer adminId;
    private String adminPassword;
    private String adminPosition;
    private Integer adminPermission;
    private String adminPhoneNumber;
    private String adminName;
}
