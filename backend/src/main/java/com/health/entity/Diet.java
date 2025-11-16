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
 * 饮食记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("diet_records")
public class Diet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 食物名称
     */
    private String foodName;

    /**
     * 食物分类 (早餐/午餐/晚餐/零食)
     */
    private String category;

    /**
     * 份量
     */
    private String quantity;

    /**
     * 卡路里 (kcal)
     */
    private BigDecimal calories;

    /**
     * 蛋白质 (克)
     */
    private BigDecimal protein;

    /**
     * 脂肪 (克)
     */
    private BigDecimal fat;

    /**
     * 碳水化合物 (克)
     */
    private BigDecimal carbohydrates;

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
