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
 * 心理健康记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mental_health_records")
public class MentalHealth implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 心情评分 (1-10)
     */
    private Integer moodScore;

    /**
     * 压力等级 (低/中/高)
     */
    private String stressLevel;

    /**
     * 焦虑程度 (1-10)
     */
    private Integer anxietyLevel;

    /**
     * 主要心理状态 (开心/平静/沮丧等)
     */
    private String mentalsState;

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
