package com.xiaojukeji.know.streaming.km.monitor.common;

import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/24
 */
public abstract class AbstractMonitorSinkTag {

    public abstract String convert2Tags();

    public abstract Map<String, Object> tagsMap();
}