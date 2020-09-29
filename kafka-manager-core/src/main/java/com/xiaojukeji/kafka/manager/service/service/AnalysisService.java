package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisBrokerDTO;

/**
 * @author huangyiminghappy@163.com, zengqiao_cn@163.com
 * @date 2019-06-14
 */
public interface AnalysisService {
     AnalysisBrokerDTO doAnalysisBroker(Long clusterId , Integer brokerId);
}
