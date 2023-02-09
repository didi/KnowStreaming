package com.xiaojukeji.know.streaming.km.common.bean.entity.param.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ConnectClusterParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wyb
 * @date 2022/12/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MirrorMakerParam extends ConnectClusterParam {

    private String mirrorMakerName;

    private String connectorType;

    List<MirrorMakerTopic> mirrorMakerTopicList;

    public MirrorMakerParam(Long connectClusterId, String connectorType, String mirrorMakerName, List<MirrorMakerTopic> mirrorMakerTopicList) {
        super(connectClusterId);
        this.mirrorMakerName = mirrorMakerName;
        this.connectorType = connectorType;
        this.mirrorMakerTopicList = mirrorMakerTopicList;
    }
}
