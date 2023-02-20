package com.xiaojukeji.know.streaming.km.biz.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterMirrorMakersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMakerCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.ClusterMirrorMakerOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerBaseStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2.MirrorMakerStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectConfigInfosVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.task.KCTaskOverviewVO;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author wyb
 * @date 2022/12/26
 */
public interface MirrorMakerManager {
    Result<Void> createMirrorMaker(MirrorMakerCreateDTO dto, String operator);

    Result<Void> deleteMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator);

    Result<Void> modifyMirrorMakerConfig(MirrorMakerCreateDTO dto, String operator);

    Result<Void> restartMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator);
    Result<Void> stopMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator);
    Result<Void> resumeMirrorMaker(Long connectClusterId, String sourceConnectorName, String operator);

    Result<MirrorMakerStateVO> getMirrorMakerStateVO(Long clusterPhyId);

    PaginationResult<ClusterMirrorMakerOverviewVO> getClusterMirrorMakersOverview(Long clusterPhyId, ClusterMirrorMakersOverviewDTO dto);


    Result<MirrorMakerBaseStateVO> getMirrorMakerState(Long connectId, String connectName);

    Result<Map<String, List<KCTaskOverviewVO>>> getTaskOverview(Long connectClusterId, String connectorName);
    Result<List<Properties>> getMM2Configs(Long connectClusterId, String connectorName);

    Result<List<ConnectConfigInfosVO>> validateConnectors(MirrorMakerCreateDTO dto);
}
