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
 * 血糖记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("blood_glucose_records")
public class BloodGlucose implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 血糖值 (mmol/L)
     */
    private Double glucoseValue;

    /**
     * 测量时间类型 (空腹/饭后/睡前)
     */
    private String measurementType;

    /**
     * 记录日期
     */
    private Date recordDate;

    /**
     * 记录时间
     */
    private String recordTime;

    /**
     * 备注
     */
    private String notes;

    private Date createTime;

    private Date updateTime;
}
