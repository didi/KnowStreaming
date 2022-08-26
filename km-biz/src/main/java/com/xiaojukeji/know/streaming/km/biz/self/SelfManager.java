package com.xiaojukeji.know.streaming.km.biz.self;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.self.SelfMetricsVO;

import java.util.Properties;

public interface SelfManager {
    Result<SelfMetricsVO> metrics();

    Result<Properties> version();
}
