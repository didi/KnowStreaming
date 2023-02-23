package com.xiaojukeji.know.streaming.km.core.service.kafkacontroller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller.KafkaController;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.kafkacontrollr.KafkaControllerPO;

import java.util.List;
import java.util.Map;

public interface KafkaControllerService {
    Result<KafkaController> getControllerFromKafka(ClusterPhy clusterPhy);

    int insertAndIgnoreDuplicateException(KafkaController kafkaController, String controllerHost, String controllerRack);

    int setNoKafkaController(Long clusterPhyId, Long triggerTime);

    KafkaController getKafkaControllerFromDB(Long clusterPhyId);

    Map<Long, KafkaController> getKafkaControllersFromDB(List<Long> clusterPhyIdList, boolean notIncludeNotAlive);

    IPage<KafkaControllerPO> pagingControllerHistories(Long clusterPhyId, Integer pageNo, Integer pageSize, String brokerHostSearchKeyword);
}
