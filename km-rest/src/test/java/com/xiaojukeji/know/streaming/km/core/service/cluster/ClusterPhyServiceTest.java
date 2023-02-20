package com.xiaojukeji.know.streaming.km.core.service.cluster;

import com.xiaojukeji.know.streaming.km.KnowStreamApplicationTest;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterPhyAddDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.config.JmxConfig;
import com.xiaojukeji.know.streaming.km.common.converter.ClusterConverter;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.DuplicateException;
import com.xiaojukeji.know.streaming.km.common.exception.ParamErrorException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Properties;

@Slf4j
public class ClusterPhyServiceTest extends KnowStreamApplicationTest {
    @Autowired
    private ClusterPhyService clusterPhyService;

    @Test
    @Order(Integer.MIN_VALUE)
    void addClusterPhyTest() {
        try {
            Properties properties = new Properties();
            JmxConfig jmxConfig = new JmxConfig();
            jmxConfig.setOpenSSL(false);

            ClusterPhyAddDTO dto = new ClusterPhyAddDTO();
            dto.setName("test");
            dto.setDescription("");
            dto.setKafkaVersion(VersionEnum.V_2_5_1.getVersion());
            dto.setJmxProperties(jmxConfig);
            dto.setClientProperties(properties);
            dto.setZookeeper(zookeeperUrl());
            dto.setBootstrapServers(bootstrapServers());
            Assertions.assertEquals(1,
                    clusterPhyService.addClusterPhy(ClusterConverter.convert2ClusterPhyPO(dto), "root"));
        } catch (ParamErrorException | DuplicateException | AdminOperateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listAllClustersTest() {
        List<ClusterPhy> clusterPhies = clusterPhyService.listAllClusters();
        Assertions.assertNotNull(clusterPhies);
        log.info("集群列表:{}", clusterPhies);
    }
}
