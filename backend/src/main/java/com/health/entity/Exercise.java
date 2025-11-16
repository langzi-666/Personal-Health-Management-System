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
 * 运动记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("exercise_records")
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 运动类型 (跑步/骑行/游泳等)
     */
    private String exerciseType;

    /**
     * 运动时长 (分钟)
     */
    private Integer duration;

    /**
     * 运动强度 (低/中/高)
     */
    private String intensity;

    /**
     * 卡路里消耗 (kcal)
     */
    private BigDecimal caloriesBurned;

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
