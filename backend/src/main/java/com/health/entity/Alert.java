package com.health.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 告警规则实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("alert_rules")
public class Alert implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 告警类型 (blood_pressure/blood_glucose/weight等)
     */
    private String alertType;

    /**
     * 告警名称
     */
    private String alertName;

    /**
     * 条件类型 (高于/低于/异常变化)
     */
    private String conditionType;

    /**
     * 阈值
     */
    private BigDecimal thresholdValue;

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
