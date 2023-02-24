package com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.Metric;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.MetricStore;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric.Metrics;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class ElasticsearchMetricStore implements MetricStore {
    private final Logger logger = LoggerFactory.getLogger(ElasticsearchMetricStore.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String hosts;
    private final String indexPrefix;
    private final String format;

    public ElasticsearchMetricStore(String hosts, String indexPrefix) {
        this(hosts, indexPrefix, "yyyy-MM-dd");
    }

    public ElasticsearchMetricStore(String hosts, String indexPrefix, String format) {
        this.hosts = hosts;
        this.indexPrefix = indexPrefix;
        this.format = format;
    }

    @Override
    public Metrics getMetrics(String clusterName, int beforeSeconds) {
        Metrics metrics = new Metrics();
        try {
            String metricsQueryJson = IOUtils.resourceToString("/MetricsQuery.json", StandardCharsets.UTF_8);
            metricsQueryJson = metricsQueryJson.replaceAll("<var_before_time>", Integer.toString(beforeSeconds))
                    .replaceAll("<var_cluster_name>", clusterName);
            try (RestClient restClient = RestClient.builder(toHttpHosts(this.hosts)).build()) {
                Request request = new Request(
                        "GET",
                        "/" + indices(beforeSeconds) + "/_search");
                request.setJsonEntity(metricsQueryJson);
                logger.debug("Es metrics query for cluster: {} request: {} dsl: {}", clusterName, request, metricsQueryJson);
                Response response = restClient.performRequest(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
                    JsonNode topics = rootNode.at("/aggregations/by_topic/buckets");
                    for (JsonNode topic : topics) {
                        String topicName = topic.path("key").asText();
                        JsonNode partitions = topic.at("/by_partition/buckets");
                        for (JsonNode partition : partitions) {
                            int partitionId = partition.path("key").asInt();
                            // double cpu = partition.at("/avg_cpu/value").asDouble();
                            double cpu = 0D;
                            double bytesIn = partition.at("/avg_bytes_in/value").asDouble();
                            double bytesOut = partition.at("/avg_bytes_out/value").asDouble();
                            double disk = partition.at("/lastest_disk/hits/hits/0/_source/metrics/LogSize").asDouble();
                            // add
                            metrics.addMetrics(new Metric(topicName, partitionId, cpu, bytesIn, bytesOut, disk));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot get metrics of cluster: " + clusterName, e);
        }
        logger.debug("Es metrics query for cluster: {} result count: {}", clusterName, metrics.values().size());
        return metrics;
    }

    private String indices(long beforeSeconds) {
        Set<String> indices = new TreeSet<>();
        DateFormat df = new SimpleDateFormat(this.format);
        long endTime = System.currentTimeMillis();
        long time = endTime - (beforeSeconds * 1000);
        while (time < endTime) {
            indices.add(this.indexPrefix + df.format(new Date(time)));
            time += 24 * 60 * 60 * 1000; // add 24h
        }
        indices.add(this.indexPrefix + df.format(new Date(endTime)));
        return String.join(",", indices);
    }

    private static HttpHost[] toHttpHosts(String url) {
        String[] nodes = url.split(",");
        HttpHost[] hosts = new HttpHost[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            String [] ipAndPort = nodes[i].split(":");
            hosts[i] = new HttpHost(ipAndPort[0], ipAndPort.length > 1 ? Integer.parseInt(ipAndPort[1]) : 9200);
        }
        return hosts;
    }

}
