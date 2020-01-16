/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-03-06      zhoubin           init.
 ********************************************************************************/
package cn.cechealth.daq.light.gossip.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 时间工具类
 * @Author: zhoubin
 * @Date: 2019-03-08 15:19
 */
public class DateUtil {

    public static LocalDate getDate(String str) {
        return getDate(str, "yyyy-MM-dd");
    }

    public static LocalDateTime getDateTime(String str) {
        return getDateTime(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static LocalDateTime getDateTime1(String str) {
        return getDateTime(str, "yyyy-MM-dd HH:mm");
    }

    public static String getCurrDateMilliTimeStr() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        return df.format(LocalDateTime.now(GLOABLE_ZONEID));
    }

    public static String getCurrTimeStr() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(LocalDateTime.now(GLOABLE_ZONEID));
    }

    public static String getCurrTimeNum() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return df.format(LocalDateTime.now(GLOABLE_ZONEID));
    }

    public static String getCurrTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return df.format(LocalDateTime.now(GLOABLE_ZONEID));
    }

    public static String format(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(LocalDateTime.ofInstant(instant, GLOABLE_ZONEID));
    }

    public static long getLongValue(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static long currentTimeMillis(){
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 全局时区
     */
    public static ZoneId GLOABLE_ZONEID =ZoneId.of("GMT+08:00");

    public static LocalDate getDate(String str, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(str, df);
    }

    public static LocalDateTime getDateTime(String str, String pattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(str, df);
    }

    public static Year getYear(String year) {
        return Year.parse(year);
    }

    public static Year getYear(int year) {
        return Year.of(year);
    }

    public static YearMonth getYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth);
    }
}
