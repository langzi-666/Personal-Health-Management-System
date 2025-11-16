package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 提醒规则实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("reminder_rules")
public class Reminder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 提醒类型 (数据记录/运动/饮水/睡眠等)
     */
    private String reminderType;

    /**
     * 提醒名称
     */
    private String reminderName;

    /**
     * 提醒时间 (HH:mm:ss)
     */
    private String reminderTime;

    /**
     * 频率 (每天/每周一/每周二等)
     */
    private String frequency;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 通知方式 (应用内/短信/邮件)
     */
    private String notificationMethod;

    private Date createTime;

    private Date updateTime;
}
