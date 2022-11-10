package com.xiaojukeji.know.streaming.km.persistence.es;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.elasticsearch.client.ESClient;
import com.didiglobal.logi.elasticsearch.client.gateway.document.ESIndexRequest;
import com.didiglobal.logi.elasticsearch.client.gateway.document.ESIndexResponse;
import com.didiglobal.logi.elasticsearch.client.model.type.ESVersion;
import com.didiglobal.logi.elasticsearch.client.request.batch.BatchNode;
import com.didiglobal.logi.elasticsearch.client.request.batch.BatchType;
import com.didiglobal.logi.elasticsearch.client.request.batch.ESBatchRequest;
import com.didiglobal.logi.elasticsearch.client.request.query.query.ESQueryRequest;
import com.didiglobal.logi.elasticsearch.client.response.batch.ESBatchResponse;
import com.didiglobal.logi.elasticsearch.client.response.batch.IndexResultItemNode;
import com.didiglobal.logi.elasticsearch.client.response.indices.catindices.CatIndexResult;
import com.didiglobal.logi.elasticsearch.client.response.indices.catindices.ESIndicesCatIndicesResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.deleteindex.ESIndicesDeleteIndexResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.gettemplate.ESIndicesGetTemplateResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.putindex.ESIndicesPutIndexResponse;
import com.didiglobal.logi.elasticsearch.client.response.indices.puttemplate.ESIndicesPutTemplateResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.setting.template.TemplateConfig;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ESOpClient {
    private static final ILog LOGGER   = LogFactory.getLog("ES_LOGGER");

    /**
     * es 地址
     */
    @Value("${es.client.address}")
    private String                          esAddress;

    /**
     * es 访问密码
     */
    @Value("${es.client.pass:''}")
    private String                          esPass;

    /**
     * 客户端个数
     */
    @Value("${es.client.client-cnt:10}")
    private Integer                         clientCnt;

    /**
     * 最大重试次数
     */
    @Value("${es.client.max-retry-cnt:5}")
    private Integer                         maxRetryCnt;

    /**
     * IO线程数
     */
    @Value("${es.client.io-thread-cnt:2}")
    private Integer                         ioThreadCnt;

    /**
     *  更新es数据的客户端连接队列
     */
    private LinkedBlockingQueue<ESClient>   esClientPool;

    private static final Integer ES_OPERATE_TIMEOUT               = 30;

    @PostConstruct
    public void init(){
        esClientPool = new LinkedBlockingQueue<>( clientCnt );

        for (int i = 0; i < clientCnt; ++i) {
            ESClient esClient = this.buildEsClient(esAddress, esPass, "", "");
            if (esClient != null) {
                this.esClientPool.add(esClient);
                LOGGER.info("class=ESOpClient||method=init||msg=add new es client {}", esAddress);
            }
        }
    }

    /**
     * 从更新es http 客户端连接池找那个获取
     *
     * @return
     */
    public ESClient getESClientFromPool() {
        ESClient esClient = null;
        int retryCount = 0;

        // 如果esClient为空或者重试次数小于5次，循环获取
        while (esClient == null && retryCount < 5) {
            try {
                ++retryCount;
                esClient = esClientPool.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (esClient == null) {
            LOGGER.error( "class=ESOpClient||method=getESClientFromPool||errMsg=fail to get es client from pool");
        }

        return esClient;
    }

    /**
     * 归还到es http 客户端连接池
     *
     * @param esClient
     */
    public void returnESClientToPool(ESClient esClient) {
        try {
            this.esClientPool.put(esClient);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 查询并获取第一个元素
     *
     * @param indexName
     * @param queryDsl
     * @param clzz
     * @param <T>
     * @return
     */
    public <T> T performRequestAndTakeFirst(String indexName, String queryDsl, Class<T> clzz) {
        List<T> hits = performRequest(indexName, queryDsl, clzz);

        if (CollectionUtils.isEmpty(hits)) {
            return null;
        }

        return hits.get(0);
    }

    /**
     * 查询并获取第一个元素
     *
     * @param indexName
     * @param queryDsl
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T performRequestAndTakeFirst(String routingValue, String indexName,
                                            String queryDsl, Class<T> clazz) {
        List<T> hits = performRequestWithRouting(routingValue, indexName, queryDsl, clazz);

        if (CollectionUtils.isEmpty(hits)) {return null;}

        return hits.get(0);
    }

    /**
     * 执行查询
     *
     * @param indexName
     * @param queryDsl
     * @return
     * @throws IOException
     */
    public ESQueryResponse performRequest(String indexName,String queryDsl) {
        return doQuery(new ESQueryRequest().indices(indexName).source(queryDsl));
    }

    public <R> R performRequest(String indexName, String queryDsl, Function<ESQueryResponse, R> func, int tryTimes) {
        ESQueryResponse esQueryResponse;
        do {
            esQueryResponse = doQuery(new ESQueryRequest().indices(indexName).source(queryDsl));
        } while (tryTimes-- > 0 && null == esQueryResponse);

        return func.apply(esQueryResponse);
    }

    public  <T> List<T> performRequest(String indexName, String queryDsl, Class<T> clzz) {
        ESQueryResponse esQueryResponse = doQuery(
                new ESQueryRequest().indices(indexName).source(queryDsl).clazz(clzz));
        if (esQueryResponse == null) {
            return new ArrayList<>();
        }

        List<Object> objectList = esQueryResponse.getSourceList();
        if (CollectionUtils.isEmpty(objectList)) {
            return new ArrayList<>();
        }

        List<T> hits = Lists.newLinkedList();
        for (Object obj : objectList) {
            hits.add((T) obj);
        }

        return hits;
    }

    public  <T> List<T> performRequestWithRouting(String routingValue, String indexName, String queryDsl, Class<T> clzz) {
        ESQueryResponse esQueryResponse = doQuery(
                new ESQueryRequest().routing(routingValue).indices(indexName).source(queryDsl).clazz(clzz));
        if (esQueryResponse == null) {
            return new ArrayList<>();
        }

        List<Object> objectList = esQueryResponse.getSourceList();
        if (CollectionUtils.isEmpty(objectList)) {
            return new ArrayList<>();
        }

        List<T> hits = Lists.newLinkedList();
        for (Object obj : objectList) {
            hits.add((T) obj);
        }

        return hits;
    }

    public <R> R performRequestWithRouting(String routingValue, String indexName,
                                           String queryDsl, Function<ESQueryResponse, R> func, int tryTimes) {
        ESQueryResponse esQueryResponse;
        do {
            esQueryResponse = doQuery(new ESQueryRequest().routing(routingValue).indices(indexName).source(queryDsl));
        } while (tryTimes-- > 0 && null == esQueryResponse);

        return func.apply(esQueryResponse);
    }

    /**
     * 写入单条
     *
     * @param source
     * @return
     */
    public boolean index(String indexName, String id, String source) {
        ESClient esClient = null;
        ESIndexResponse response = null;

        try {
            esClient = getESClientFromPool();
            if (esClient == null) {
                return false;
            }

            ESIndexRequest esIndexRequest = new ESIndexRequest();
            esIndexRequest.setIndex(indexName);
            esIndexRequest.source(source);
            esIndexRequest.id(id);

            for (int i = 0; i < this.maxRetryCnt; ++i) {
                response = esClient.index(esIndexRequest).actionGet(10, TimeUnit.SECONDS);
                if (response == null) {
                    continue;
                }

                return response.getRestStatus().getStatus() == HttpStatus.SC_OK
                        || response.getRestStatus().getStatus() == HttpStatus.SC_CREATED;
            }

        } catch (Exception e) {
            LOGGER.warn(
                    "class=ESOpClient||method=index||indexName={}||id={}||source={}||errMsg=index doc error. ",
                    indexName, id, source, e);
            if (response != null) {
                LOGGER.warn(
                        "class=ESOpClient||method=index||indexName={}||id={}||source={}||errMsg=response {}",
                        indexName, id, source, JSON.toJSONString(response));
            }
        } finally {
            if (esClient != null) {
                returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**
     * 批量写入
     *
     * @param indexName
     * @return
     */
    public boolean batchInsert(String indexName, List<? extends BaseESPO> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return true;
        }

        ESClient esClient = null;
        ESBatchResponse response = null;
        try {
            esClient = getESClientFromPool();
            if (esClient == null) {
                return false;
            }

            ESBatchRequest batchRequest = new ESBatchRequest();
            for (BaseESPO po : pos) {
                //write with routing
                if (null != po.getRoutingValue()) {
                    BatchNode batchNode = new BatchNode(BatchType.INDEX, indexName, null, po.getKey(), JSON.toJSONString(po));
                    batchNode.setRouting(po.getRoutingValue());
                    batchNode.setEsVersion(ESVersion.ES760);
                    batchRequest.addNode(batchNode);
                    continue;
                }

                //write without routing
                batchRequest.addNode(BatchType.INDEX, indexName, null, po.getKey(), JSON.toJSONString(po));
            }

            for (int i = 0; i < this.maxRetryCnt; ++i) {
                response = esClient.batch(batchRequest).actionGet(2, TimeUnit.MINUTES);
                if (response == null) {continue;}

                if (handleErrorResponse(indexName, pos, response)) {return false;}

                return response.getRestStatus().getStatus() == HttpStatus.SC_OK && !response.getErrors();
            }
        } catch (Exception e) {
            LOGGER.warn(
                    "method=batchInsert||indexName={}||errMsg=batch insert error. ", indexName, e);
            if (response != null) {
                LOGGER.warn("method=batchInsert||indexName={}||errMsg=response {}", indexName, JSON.toJSONString(response));
            }

        } finally {
            if (esClient != null) {
                returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**
     * 根据表达式判断索引是否已存在
     */
    public boolean indexExist(String indexName) {
        ESClient esClient = null;
        try {
            esClient = this.getESClientFromPool();
            if (esClient == null) {
                return false;
            }

            // 检查索引是否存在
            return esClient.admin().indices().prepareExists(indexName).execute().actionGet(30, TimeUnit.SECONDS).isExists();
        } catch (Exception e){
            LOGGER.warn("class=ESOpClient||method=indexExist||indexName={}||msg=exception!", indexName, e);
        } finally {
            if (esClient != null) {
                returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**
     * 创建索引
     */
    public boolean createIndex(String indexName) {
        if (indexExist(indexName)) {
            return true;
        }

        ESClient client = getESClientFromPool();
        if (client != null) {
            try {
                ESIndicesPutIndexResponse response = client.admin().indices().preparePutIndex(indexName).execute()
                        .actionGet(ES_OPERATE_TIMEOUT, TimeUnit.SECONDS);
                return response.getAcknowledged();
            } catch (Exception e){
                LOGGER.warn( "msg=create index fail||indexName={}", indexName, e);
            } finally {
                returnESClientToPool(client);
            }
        }

        return false;
    }

    public boolean templateExist(String indexTemplateName){
        ESClient esClient = null;

        try {
            esClient = this.getESClientFromPool();

            // 获取es中原来index template的配置
            ESIndicesGetTemplateResponse getTemplateResponse =
                    esClient.admin().indices().prepareGetTemplate( indexTemplateName ).execute().actionGet( ES_OPERATE_TIMEOUT, TimeUnit.SECONDS );

            TemplateConfig templateConfig = getTemplateResponse.getMultiTemplatesConfig().getSingleConfig();

            if (null != templateConfig) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn( "method=templateExist||indexTemplateName={}||msg=exception!",
                    indexTemplateName, e);
        } finally {
            if (esClient != null) {
                this.returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**
     * 创建索引模板
     */
    public boolean createIndexTemplateIfNotExist(String indexTemplateName, String config) {
        ESClient esClient = null;

        try {
            esClient = this.getESClientFromPool();

            //存在模板就返回，不存在就创建
            if(templateExist(indexTemplateName)){return true;}

            // 创建新的模板
            ESIndicesPutTemplateResponse response = esClient.admin().indices().preparePutTemplate( indexTemplateName )
                    .setTemplateConfig( config ).execute().actionGet( ES_OPERATE_TIMEOUT, TimeUnit.SECONDS );

            return response.getAcknowledged();
        } catch (Exception e) {
            LOGGER.warn( "method=createIndexTemplateIfNotExist||indexTemplateName={}||config={}||msg=exception!",
                    indexTemplateName, config, e
            );
        } finally {
            if (esClient != null) {
                this.returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**
     * 根据索引模板获取所有的索引
     * @param indexName
     * @return
     */
    public List<String> listIndexByName(String indexName){
        ESClient esClient = null;

        try {
            esClient = this.getESClientFromPool();

            ESIndicesCatIndicesResponse response = esClient.admin().indices().prepareCatIndices(indexName + "*").execute()
                    .actionGet(ES_OPERATE_TIMEOUT, TimeUnit.SECONDS);

            if(null != response){
                return response.getCatIndexResults().stream().map(CatIndexResult::getIndex).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.warn( "method=listIndexByTemplate||indexName={}||msg=exception!",
                    indexName, e);
        } finally {
            if (esClient != null) {
                this.returnESClientToPool(esClient);
            }
        }

        return new ArrayList<>();
    }

    /**
     * 删除索引
     * @param indexRealName
     * @return
     */
    public boolean delIndexByName(String indexRealName){
        ESClient esClient = null;

        try {
            esClient = this.getESClientFromPool();

            ESIndicesDeleteIndexResponse response = esClient.admin().indices().prepareDeleteIndex(indexRealName).execute()
                    .actionGet(ES_OPERATE_TIMEOUT, TimeUnit.SECONDS);
            return response.getAcknowledged();
        } catch (Exception e) {
            LOGGER.warn( "method=delIndexByName||indexRealName={}||msg=exception!",
                    indexRealName, e);
        } finally {
            if (esClient != null) {
                this.returnESClientToPool(esClient);
            }
        }

        return false;
    }

    /**************************************************** private method ****************************************************/

    /**
     * 执行查询
     * @param request
     * @return
     */
    @Nullable
    private ESQueryResponse doQuery(ESQueryRequest request) {
        ESClient esClient = null;
        try {
            esClient = getESClientFromPool();
            ESQueryResponse response = esClient.query(request).actionGet(120, TimeUnit.SECONDS);

            if(!EnvUtil.isOnline()){
                LOGGER.info("method=doQuery||indexName={}||queryDsl={}||ret={}",
                        request.indices(), bytesReferenceConvertDsl(request.source()), JSON.toJSONString(response));
            }

            return response;
        } catch (Exception e) {
            LOGGER.error( "method=doQuery||indexName={}||queryDsl={}||errMsg=query error. ",
                    request.indices(), bytesReferenceConvertDsl(request.source()), e);
            return null;
        }finally {
            if (esClient != null) {
                returnESClientToPool(esClient);
            }
        }
    }

    private boolean handleErrorResponse(String indexName, List<? extends BaseESPO> pos, ESBatchResponse response) {
        if (response.getErrors().booleanValue()) {
            int errorItemIndex = 0;

            if (CollectionUtils.isNotEmpty(response.getItems())) {
                for (IndexResultItemNode item : response.getItems()) {
                    recordErrorResponseItem(indexName, pos, errorItemIndex++, item);
                }
            }

            return true;
        }

        return false;
    }

    private void recordErrorResponseItem(String indexName, List<? extends BaseESPO> pos, int errorItemIndex, IndexResultItemNode item) {
        if (item.getIndex() != null && item.getIndex().getShards() != null
                && CollectionUtils.isNotEmpty(item.getIndex().getShards().getFailures())) {
            LOGGER.warn(
                    "class=ESOpClient||method=batchInsert||indexName={}||errMsg=Failures: {}, content: {}",
                    indexName, item.getIndex().getShards().getFailures().toString(),
                    JSON.toJSONString(pos.get(errorItemIndex)));
        }

        if (item.getIndex() != null && item.getIndex().getError() != null) {
            LOGGER.warn(
                    "class=ESOpClient||method=batchInsert||indexName={}||errMsg=Error: {}, content: {}",
                    indexName, item.getIndex().getError().getReason(),
                    JSON.toJSONString(pos.get(errorItemIndex)));
        }
    }

    /**
     * 转换dsl语句
     *
     * @param bytes
     * @return
     */
    private String bytesReferenceConvertDsl(BytesReference bytes) {
        try {
            return XContentHelper.convertToJson(bytes, false);
        } catch (IOException e) {
            LOGGER.warn("class=ESOpClient||method=bytesReferenceConvertDsl||errMsg=fail to covert", e);
        }

        return "";
    }

    private ESClient buildEsClient(String address,String password,String clusterName, String version) {
        if (StringUtils.isBlank(address)) {
            return null;
        }

        ESClient esClient = new ESClient();
        try {
            esClient.addTransportAddresses(address);

            if(StringUtils.isNotBlank(clusterName)) {
                esClient.setClusterName(clusterName);
            }
            if(StringUtils.isNotBlank(version)){
                esClient.setEsVersion(version);
            }
            if(StringUtils.isNotBlank(password)){
                esClient.setPassword(password);
            }
            if(this.ioThreadCnt > 0) {
                esClient.setIoThreadCount( this.ioThreadCnt );
            }

            // 配置http超时
            esClient.setRequestConfigCallback(builder -> builder.setConnectTimeout(10000).setSocketTimeout(120000)
                    .setConnectionRequestTimeout(120000));
            esClient.start();

            return esClient;
        } catch (Exception e) {
            try {
                esClient.close();
            } catch (Exception innerE) {
                // ignore
            }

            LOGGER.error("class=ESESOpClient||method=buildEsClient||errMsg={}||address={}", e.getMessage(), address, e);
            return null;
        }
    }
}
