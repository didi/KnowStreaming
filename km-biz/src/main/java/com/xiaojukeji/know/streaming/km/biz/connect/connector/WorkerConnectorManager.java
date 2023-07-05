package com.xiaojukeji.know.streaming.km.biz.connect.connector;


import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;

import java.util.List;

/**
 * @author wyb
 * @date 2022/11/14
 */
public interface WorkerConnectorManager {
    Result<List<KCTaskOverviewVO>> getTaskOverview(Long connectClusterId, String connectorName);

}
