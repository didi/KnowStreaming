package com.xiaojukeji.kafka.manager.common.utils;

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
}
