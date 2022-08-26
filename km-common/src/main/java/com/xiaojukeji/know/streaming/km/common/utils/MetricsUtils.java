package com.xiaojukeji.know.streaming.km.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by linyunan on 2021-08-05
 */
public class MetricsUtils {
    private static final long ONE_HOUR      = 60 * 60 * 1000L;
    private static final long THREE_HOUR    = 3 * 60 * 60 * 1000L;
    private static final long SIX_HOUR      = 6 * 60 * 60 * 1000L;
    private static final long ONE_DAY       = 24 * 60 * 60 * 1000L;
    private static final long SEVEN_DAY     = 7 * 24 * 60 * 60 * 1000L;
    private static final char COLON         = ':';
    private static final char SPACE         = ' ';

    public static String getInterval(Long intervalTime) {
        if (intervalTime > 0 && intervalTime <= SIX_HOUR) {
            return Interval.ONE_MIN.getStr();
        } else if (intervalTime > SIX_HOUR && intervalTime <= ONE_DAY) {
            return Interval.TEN_MIN.getStr();
        } else if (intervalTime > ONE_DAY && intervalTime <= SEVEN_DAY) {
            return Interval.ONE_HOUR.getStr();
        } else if (intervalTime > SEVEN_DAY) {
            return Interval.ONE_HOUR.getStr();
        } else {
            return Interval.ONE_HOUR.getStr();
        }
    }

    /**
     * 获取时间点对应聚合时间段
     * @param intervalTime
     * @param timePoint
     * @return
     */
    public static Tuple<Long, Long> getSortInterval(Long intervalTime, long timePoint) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timePoint);
        cal.set( Calendar.SECOND, 0);
        cal.set( Calendar.MILLISECOND, 0);
        if (intervalTime > 0 && intervalTime <= ONE_HOUR) {
            long tuple1 = cal.getTimeInMillis();
            return new Tuple<>(tuple1 - 60 * 1000, tuple1);
        } else if (intervalTime > ONE_HOUR && intervalTime <= ONE_DAY) {
            int minute = cal.get( Calendar.MINUTE);
            int start = 0;
            int end = 20;
            if (minute > 20 && minute <= 40) {
                start = 20;
                end = 40;
            } else if (minute > 40 && minute <= 60){
                start = 40;
                end = 60;
            }
            cal.set( Calendar.MINUTE, start);
            long tuple1 = cal.getTimeInMillis();
            cal.set( Calendar.MINUTE, end);
            long tuple2 = cal.getTimeInMillis();
            return new Tuple<>(tuple1, tuple2);
        } else  {
            cal.set( Calendar.MINUTE, 0);
            long tuple1 = cal.getTimeInMillis();
            return new Tuple<>(tuple1, tuple1 + 60 * 60 * 1000);
        }
    }

    /**
     * 获取时刻所属的聚合区间, 如按20m间隔, 则聚合区间在0 ~ 20 , 20 ~ 40 , 40 ~ 60
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param delayTime  针对分钟级别间隔的延迟时间 20分钟和小时级别不做延迟
     * @return
     */
    public static Tuple<Long, Long> getSortInterval(Long startTime, Long endTime, Long delayTime) {
        String dateTimeStr = DateUtils.getDateTimeStr(endTime);
        String[] dateTimeArr = StringUtils.split(dateTimeStr, SPACE);
        String date         = null;
        String startHourStr = null;
        String endHourStr;
        String startMinuteStr = null;
        String endMinuteStr   = null;
        if (dateTimeArr.length > 0) {
            date = dateTimeArr[0];
            String time = dateTimeArr[1];
            String[] timeArr = StringUtils.split(time, COLON);
            if (timeArr.length > 1) {
                startHourStr   = timeArr[0];
                startMinuteStr = timeArr[1];
            }
        }

        String interval = getInterval(endTime - startTime);
        if (Interval.ONE_MIN.getStr().equals(interval)) {
            endHourStr   = startHourStr;
            endMinuteStr = String.valueOf( Integer.valueOf(startMinuteStr) + 1);
            return buildIntervalTuple(date, startHourStr, endHourStr, startMinuteStr, endMinuteStr, delayTime);
        } else if (Interval.TWENTY_MIN.getStr().equals(interval)) {
            int startMinute = Integer.valueOf(startMinuteStr);
            endHourStr = startHourStr;
            if (0 <= startMinute && startMinute <= 20) {
                startMinuteStr = "00";
                endMinuteStr   = "20";
            } else if (20 < startMinute && startMinute <= 40) {
                startMinuteStr = "20";
                endMinuteStr   = "40";
            } else if (40 < startMinute && startMinute <= 59) {
                startMinuteStr = "40";
                endMinuteStr   = "60";
            }
            return buildIntervalTuple(date, startHourStr, endHourStr, startMinuteStr, endMinuteStr,null);
        } else if (Interval.ONE_HOUR.getStr().equals(interval)) {
            endHourStr = String.valueOf( Integer.valueOf(startHourStr) + 1);
            startMinuteStr = "00";
            endMinuteStr   = "00";
            return buildIntervalTuple(date, startHourStr, endHourStr, startMinuteStr, endMinuteStr,null);
        } else {
            return new Tuple<>();
        }
    }

    public static Tuple<Long, Long> buildIntervalTuple(String date, String startHourStr, String endHourStr,
                                                       String startMinuteStr, String endMinuteStr, Long delayTime) {
        Tuple<Long, Long> t           = new Tuple<>();
        StringBuilder startDateSb = new StringBuilder();
        StringBuilder endDateSb   = new StringBuilder();

        startDateSb.append(date).append(" ").append(startHourStr).append(":").append(startMinuteStr).append(":").append("00");
        endDateSb.append(date).append(" ").append(endHourStr).append(":").append(endMinuteStr).append(":").append("00");
        Long startTime = DateUtils.getTimeEpochMilli(startDateSb.toString());
        Long endTime = DateUtils.getTimeEpochMilli(endDateSb.toString());

        if(null != delayTime) {
            startTime = startTime - delayTime;
            endTime   = endTime   - delayTime;
        }

        t.setV1(startTime);
        t.setV2(endTime);
        return t;
    }

    public static Double getDoubleValuePerMin(String interval, String value) {
        if (Interval.ONE_MIN.getStr().equals(interval)) {
            return Double.valueOf(value);
        } else if (Interval.TWENTY_MIN.getStr().equals(interval)) {
            return Double.valueOf(value) / 20.00;
        } else if (Interval.ONE_HOUR.getStr().equals(interval)) {
            return Double.valueOf(value) / 60.00;
        }
        return Double.valueOf(value);
    }

    /**
     * 计算一段时间内，时间分片
     * @param startTime
     * @param endTime
     * @param interval
     * @param dateUnit   Calendar.MINUTE||Calendar.SECOND||Calendar.HOUR
     * @return
     */
    public static List<Long> timeRange(long startTime, long endTime, long interval, int dateUnit) {
        long step = interval * 1000 * 60;
        if (dateUnit == Calendar.HOUR) {
            step = interval * 1000 * 60 * 60;
        } else if (dateUnit == Calendar.SECOND) {
            step = interval * 1000;
        }
        startTime = startTime / 1000 * 1000;
        endTime = endTime / 1000 * 1000;
        if (endTime < startTime) {
            return new ArrayList<>(0);
        }

        List<Long> list = Lists.newArrayList();
        while (endTime >= startTime) {
            list.add(endTime);
            endTime -= step;
        }

        return list;
    }

    public enum MetricsTimeType {

                                 MINUTE("minute"), TWENTY_MINUTES("twentyMinutes"), HOUR("hour");

        private String str;

        private MetricsTimeType(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public static MetricsUtils.MetricsTimeType getByStr(String str) {
            for (MetricsUtils.MetricsTimeType type : MetricsUtils.MetricsTimeType.values()) {
                if (type.str.equalsIgnoreCase(str)) {
                    return type;
                }
            }

            return null;
        }
    }

    public enum Interval {

                          ONE_MIN("1m"), TEN_MIN("10m"), TWENTY_MIN("20m"), ONE_HOUR("1h");

        private String str;

        private Interval(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public static MetricsUtils.Interval getByStr(String str) {
            for (MetricsUtils.Interval type : MetricsUtils.Interval.values()) {
                if (type.str.equalsIgnoreCase(str)) {
                    return type;
                }
            }

            return null;
        }
    }

}
