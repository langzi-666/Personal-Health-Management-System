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
 * 睡眠记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sleep_records")
public class Sleep implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 入睡时间
     */
    private String bedTime;

    /**
     * 起床时间
     */
    private String wakeTime;

    /**
     * 睡眠时长 (小时)
     */
    private Double sleepDuration;

    /**
     * 睡眠质量 (1-10)
     */
    private Integer sleepQuality;

    /**
     * 记录日期
     */
    private Date recordDate;

    /**
     * 备注
     */
    private String notes;

    private Date createTime;

    private Date updateTime;
}
