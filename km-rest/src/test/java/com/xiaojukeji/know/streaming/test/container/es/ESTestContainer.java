package com.xiaojukeji.know.streaming.test.container.es;

import com.xiaojukeji.know.streaming.test.container.BaseTestContainer;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Supplier;

public class ESTestContainer extends BaseTestContainer {

    // es容器
    private static final ElasticsearchContainer ES_CONTAINER = new ElasticsearchContainer(
            DockerImageName.parse("docker.io/library/elasticsearch:7.6.2").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")
    )
            .withEnv("TZ", "Asia/Shanghai")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withEnv("discovery.type", "single-node");

    @NotNull
    public Supplier<Object> esUrl() {
        return () -> ES_CONTAINER.getHost() + ":" + ES_CONTAINER.getMappedPort(9200);
    }

    @Override
    public void init() {
        Startables.deepStart(ES_CONTAINER).join();
    }

    @Override
    public void cleanup() {
    }
}
