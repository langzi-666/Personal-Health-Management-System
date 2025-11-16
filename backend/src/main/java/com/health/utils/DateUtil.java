package com.health.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtil {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
    private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);

    /**
     * 格式化日期 (yyyy-MM-dd)
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.text.SimpleDateFormat(DEFAULT_DATE_PATTERN).format(date);
    }

    /**
     * 格式化日期 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return new java.text.SimpleDateFormat(DEFAULT_DATETIME_PATTERN).format(date);
    }

    /**
     * 格式化LocalDate
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(dateFormatter);
    }

    /**
     * 格式化LocalDateTime
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(datetimeFormatter);
    }

    /**
     * 解析日期字符串 (yyyy-MM-dd)
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return new java.text.SimpleDateFormat(DEFAULT_DATE_PATTERN).parse(dateStr);
        } catch (Exception e) {
            log.error("Failed to parse date: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return new java.text.SimpleDateFormat(DEFAULT_DATETIME_PATTERN).parse(dateTimeStr);
        } catch (Exception e) {
            log.error("Failed to parse datetime: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前日期
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前日期字符串 (yyyy-MM-dd)
     */
    public static String getCurrentDateStr() {
        return formatDate(new Date());
    }

    /**
     * 获取当前日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentDateTimeStr() {
        return formatDateTime(new Date());
    }

    /**
     * 计算两个日期之间的天数
     */
    public static Long getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    /**
     * 检查是否是同一天
     */
    public static Boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return formatDate(date1).equals(formatDate(date2));
    }
}
