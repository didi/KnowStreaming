package com.xiaojukeji.know.streaming.km.core.service.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.connect.ConnectorPO;

import java.util.List;
import java.util.Map;

/**
 * @author wyb
 * @date 2022/12/14
 */
public interface MirrorMakerService {
    Result<Map<String, MirrorMakerTopic>> getMirrorMakerTopicMap(Long connectClusterId);

    List<MirrorMakerTopic> getMirrorMakerTopicList(ConnectorPO mirrorMaker, Map<String, MirrorMakerTopic> mirrorMakerTopicMap);
}
