package com.xiaojukeji.kafka.manager.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 * @author huangyiminghappy@163.com
 * @date 2019-03-20
 */
public class DateUtils {
    public static Date long2Date(Long time){
        return new Date(time);
    }

    /**
     * 获取nDay的起始时间
     */
    public static Long getDayStarTime(int nDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, nDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    /**
     * 任意日期所在月的第一天的起始时间
     * @param date 任意日期
     * @author zengqiao
     * @date 19/10/30
     * @return java.util.Date
     */
    public static Date getMonthStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
        );
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 任意日期所在月的最后一天的最后时间
     * @param date 任意日期
     * @author zengqiao
     * @date 19/10/30
     * @return java.util.Date
     */
    public static Date getMonthEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
        );
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    public static String getFormattedDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date.getTime());
    }

    public static String getFormattedDate(Long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd").format(timestamp);
    }

    public static Integer compare(Date t1, Date t2){
        return Long.compare(t1.getTime(), t2.getTime());
    }
}
