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
 * 血压记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("blood_pressure_records")
public class BloodPressure implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收缩压 (mmHg)
     */
    private Integer systolic;

    /**
     * 舒张压 (mmHg)
     */
    private Integer diastolic;

    /**
     * 脉搏 (次/分)
     */
    private Integer pulse;

    /**
     * 测量位置 (如：左臂、右臂)
     */
    private String measurementLocation;

    /**
     * 血压状态 (正常/偏高/高血压)
     */
    private String status;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
