package com.xiaojukeji.know.streaming.km.biz.version;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.UserMetricConfigDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.metric.UserMetricConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.version.VersionItemVO;

import java.util.List;
import java.util.Map;

public interface VersionControlManager {

    /**
     * 查询当前所有的兼容性(指标、前端操作)配置信息
     * @return
     */
    Result<Map<String, VersionItemVO>> listAllVersionItem();

    /**
     * 获取当前ks所有支持的kafka版本
     * @return
     */
    Result<Map<String, Long>> listAllKafkaVersions();

    /**
     * 获取全部集群 clusterId 中类型为 type 的指标，不论支持不支持
     * @param clusterId
     * @param type
     * @return
     */
    Result<List<VersionItemVO>> listKafkaClusterVersionControlItem(Long clusterId, Integer type);

    /**
     * 获取当前用户设置的用于展示的指标配置
     * @param clusterId
     * @param type
     * @param operator
     * @return
     */
    Result<List<UserMetricConfigVO>> listUserMetricItem(Long clusterId, Integer type, String operator);

    /**
     * 更新用户配置的指标项
     * @param clusterId
     * @param type
     * @param userMetricConfigDTO
     * @param operator
     * @return
     */
    Result<Void> updateUserMetricItem(Long clusterId, Integer type,
                                        UserMetricConfigDTO userMetricConfigDTO, String operator);
}
