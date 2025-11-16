package com.health.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页工具类
 */
@Data
public class PageUtil<T> {

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 创建分页对象
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return MyBatis Plus分页对象
     */
    public static <T> Page<T> createPage(Long current, Long size) {
        if (current == null || current < 1) {
            current = 1L;
        }
        if (size == null || size < 1) {
            size = 10L;
        }
        // 限制每页最大数量
        if (size > 100) {
            size = 100L;
        }
        return new Page<>(current, size);
    }

    /**
     * 将MyBatis Plus分页结果转换为PageUtil
     *
     * @param page MyBatis Plus分页结果
     * @param <T>  数据类型
     * @return PageUtil对象
     */
    public static <T> PageUtil<T> fromIPage(IPage<T> page) {
        PageUtil<T> pageUtil = new PageUtil<>();
        pageUtil.setCurrent(page.getCurrent());
        pageUtil.setSize(page.getSize());
        pageUtil.setTotal(page.getTotal());
        pageUtil.setPages(page.getPages());
        pageUtil.setRecords(page.getRecords());
        return pageUtil;
    }

    /**
     * 创建空分页结果
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return PageUtil对象
     */
    public static <T> PageUtil<T> empty(Long current, Long size) {
        PageUtil<T> pageUtil = new PageUtil<>();
        pageUtil.setCurrent(current != null ? current : 1L);
        pageUtil.setSize(size != null ? size : 10L);
        pageUtil.setTotal(0L);
        pageUtil.setPages(0L);
        pageUtil.setRecords(List.of());
        return pageUtil;
    }
}

