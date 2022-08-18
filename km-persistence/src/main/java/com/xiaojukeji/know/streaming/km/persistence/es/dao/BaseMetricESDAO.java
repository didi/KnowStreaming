package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.Maps;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BaseMetricESPO;
import com.xiaojukeji.know.streaming.km.common.enums.metric.KafkaMetricIndexEnum;
import com.xiaojukeji.know.streaming.km.common.utils.IndexNameUtils;
import com.xiaojukeji.know.streaming.km.persistence.es.BaseESDAO;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslsConstant;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;

@NoArgsConstructor
public class BaseMetricESDAO extends BaseESDAO {

    /**
     * 操作的索引名称
     */
    protected String                indexName;

    protected static final Long     ONE_MIN =  60 * 1000L;
    protected static final Long     FIVE_MIN = 5  * ONE_MIN;
    protected static final Long     ONE_HOUR = 60 * ONE_MIN;
    protected static final Long     ONE_DAY  = 24 * ONE_HOUR;

    /**
     * 不同维度 kafka 监控数据
     */
    private static Map<KafkaMetricIndexEnum, BaseMetricESDAO> ariusStatsEsDaoMap    = Maps
            .newConcurrentMap();

    public static BaseMetricESDAO getByStatsType(KafkaMetricIndexEnum statsType) {
        return ariusStatsEsDaoMap.get(statsType);
    }

    /**
     * 注册不同维度数据对应操作的es类
     *
     * @param statsType
     * @param baseAriusStatsEsDao
     */
    public static void register(KafkaMetricIndexEnum statsType, BaseMetricESDAO baseAriusStatsEsDao) {
        ariusStatsEsDaoMap.put(statsType, baseAriusStatsEsDao);
    }

    /**
     * 批量插入索引统计信息

     * @param statsInfo
     * @return
     */
    public boolean batchInsertStats(List<? extends BaseESPO> statsInfo) {
        String realIndex = IndexNameUtils.genCurrentDailyIndexName(indexName);
        return esOpClient.batchInsert(realIndex, statsInfo);
    }

    public String realIndex(long startTime, long endTime){
        return IndexNameUtils.genDailyIndexName(indexName, startTime, endTime);
    }

    /**
     * 构建如下dsl语句
     * {
     *   "FIELD": {
     *     "order": "desc"
     *   }
     * }
     */
    public String buildSortDsl(SearchSort sort, SearchSort def){
        if(null == sort || !sort.valid()){sort = def;}

        Map<String, String> orderMap = new HashMap<>();
        orderMap.put(ORDER, sort.isDesc() ? DESC : ASC);

        String fieldName = sort.isMetric() ? METRICS_DOT + sort.getQueryName() : sort.getQueryName();

        Map<String, Map<String, String>> fieldMap = new HashMap<>();
        fieldMap.put(fieldName, orderMap);

        return JSON.toJSONString(fieldMap);
    }

    /**
     * 构建如下dsl语句
     * {
     *   "range": {
     *     "timestamp": {
     *        "gte": 1646984254883,
     *        "lte": 1646987854883
     *      }
     *    }
     *  }
     *}
     */
    public String buildRangeDsl(SearchRange range){
        if(null == range || !range.valid()){return "";}

        Map<String, Float> glTEMap = new HashMap<>();
        glTEMap.put(GTE, range.getMin());
        glTEMap.put(LTE, range.getMax());

        String fieldName = range.getRealMetricName();

        Map<String, Map<String, Float>> rangeKeyMap = new HashMap<>();
        rangeKeyMap.put(fieldName, glTEMap);

        Map<String, Map<String, Map<String, Float>>> rangeMap = new HashMap<>();
        rangeMap.put(RANGE, rangeKeyMap);

        return JSON.toJSONString(rangeMap);
    }

    /**
     * 构建如下dsl语句
     * {
     *   "match": {
     *     "groupName": "g-know-streaming-123456"
     *     }
     * },
     * {
     *   "match": {
     *     "groupName": "g-know-streaming-123456"
     *     }
     *  }
     */
    public String buildMatchDsl(List<SearchTerm> matches){
        if(CollectionUtils.isEmpty(matches)){return "";}

        List<Map<String, Map<String, Object>>> list = new ArrayList<>();

        for(SearchTerm match : matches){
            if(null == match || !match.valid()){continue;}

            String fieldName = match.getRealMetricName();

            Map<String, Object> matchItem = new HashMap<>();
            matchItem.put(fieldName, match.getQueryValue());

            Map<String, Map<String, Object>> matchMap = new HashMap<>();
            matchMap.put(MATCH, matchItem);

            list.add(matchMap);
        }

        String json = JSON.toJSONString(list);
        return json.substring(1, json.length() - 1);
    }

    /**
     * {
     *  "term": {
     *     "clusterPhyId": {
     *       "value": 2
     *     }
     *   }
     * },
     * {
     *   "term": {
     *     "timestamp": {
     *      "value": 1649845260000
     *     }
     *   }
     * }
     * @param terms
     * @return
     */
    public String buildTermsDsl(List<SearchTerm> terms){
        if(CollectionUtils.isEmpty(terms)){return "";}

        List<String> termStrList = new ArrayList<>();
        for(SearchTerm match : terms){
            if(null == match || !match.valid()){continue;}

            String fieldName = match.getRealMetricName();

            termStrList.add(buildTermDsl(fieldName, match.getQueryValue()));
        }

        if(CollectionUtils.isEmpty(termStrList)){return "";}

        return String.join(",", termStrList);
    }

    /**
     * {
     *  "bool": {
     *    "should": [
     *      {
     *         "term": {
     *          "clusterPhyId": {
     *            "value": "1"
     *          }
     *         }
     *       },
     *       {
     *         "term": {
     *           "clusterPhyId": {
     *             "value": "2"
     *           }
     *         }
     *       }
     *     ]
     *   }
     * }
     * @param shoulds
     * @return
     */
    public String buildShouldDsl(List<SearchShould> shoulds){
        if(CollectionUtils.isEmpty(shoulds)){return "";}

        List<String> fieldValueStrList = new ArrayList<>();
        for(SearchShould should : shoulds){
            if(null == should || !should.valid()){return "";}

            String fieldName = should.getRealMetricName();

            for(Object fieldValue : should.getQueryValues()){
                fieldValueStrList.add(buildTermDsl(fieldName, fieldValue));
            }
        }

        String format = "{\"bool\":{\"should\": [%s]}}";
        String fieldValueStr = String.join(",", fieldValueStrList);

        return String.format(format, fieldValueStr);
    }

    public String buildShouldDsl(SearchShould should){
        return buildShouldDsl( Arrays.asList(should));
    }

    protected String buildTermDsl(String fieldName, Object fieldValue){
        Map<String, Object> valueItem = new HashMap<>();
        valueItem.put(VALUE, fieldValue);

        Map<String, Map<String, Object>> filedMap = new HashMap<>();
        filedMap.put(fieldName, valueItem);

        Map<String, Map<String, Map<String, Object>>> termMap = new HashMap<>();
        termMap.put(TERM, filedMap);

        return JSON.toJSONString(termMap);
    }

    public String buildPrefixDsl(SearchFuzzy fuzzy){
        if(null == fuzzy || !fuzzy.valid()){return "";}

        String fieldName = fuzzy.getRealMetricName();

        Map<String, Object> valueItem = new HashMap<>();
        valueItem.put(VALUE, fuzzy.getQueryValue());

        Map<String, Map<String, Object>> filedMap = new HashMap<>();
        filedMap.put(fieldName, valueItem);

        Map<String, Map<String, Map<String, Object>>> termMap = new HashMap<>();
        termMap.put(PREFIX, filedMap);

        return JSON.toJSONString(termMap);
    }

    public String buildAggsDSL(List<String> metrics, String aggType) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < metrics.size(); i++) {
            String metricName = metrics.get(i);

            Map<String, String> aggsSubSubCellMap = Maps.newHashMap();
            aggsSubSubCellMap.put(FIELD, METRICS_DOT + metricName);

            buildAggsDslMap(aggType, sb, metricName, aggsSubSubCellMap);
            if (i != metrics.size() - 1) {
                sb.append(",").append("\n");
            }
        }

        return sb.toString();
    }

    public void buildAggsDslMap(String aggType, StringBuilder sb, String metricName,
                                 Map<String, String> aggsSubSubCellMap) {
        Map<String, Object> aggsSubCellMap = Maps.newHashMap();
        aggsSubCellMap.put(aggType, aggsSubSubCellMap);

        Map<String, Object> aggsCellMap = Maps.newHashMap();
        aggsCellMap.put(metricName, aggsSubCellMap);

        JSONObject jsonObject = new JSONObject(aggsCellMap);
        String str = jsonObject.toJSONString();
        sb.append(str, 1, str.length() - 1);
    }

    protected Map<String, ESAggr> checkBucketsAndHitsOfResponseAggs(ESQueryResponse response){
        if(null == response || null == response.getAggs()){
            return null;
        }

        Map<String, ESAggr> esAggrMap = response.getAggs().getEsAggrMap();
        if (null == esAggrMap || null == esAggrMap.get(HIST)) {
            return null;
        }

        if(CollectionUtils.isEmpty(esAggrMap.get(HIST).getBucketList())){
            return null;
        }

        return esAggrMap;
    }

    protected int handleESQueryResponseCount(ESQueryResponse response){
        if(null == response || null == response.getHits()
                || null ==response.getHits().getUnusedMap()){return -1;}

        return Integer.valueOf(response.getHits().getUnusedMap().getOrDefault(TOTAL, 0).toString());
    }

    protected <T extends BaseMetricESPO> T filterMetrics(T t, List<String> metricNames){
        t.setMetrics(t.getMetrics(metricNames));
        return t;
    }

    protected <T extends BaseMetricESPO> List<T> filterMetrics(List<T> ts, List<String> metricNames){
        ts.stream().forEach(t -> filterMetrics(t, metricNames));
        return ts;
    }

    /**
     * 获取有数据的第一个时间点
     */
    protected Long getLatestMetricTime() {
        return getLatestMetricTime("");
    }

    protected Long getLatestMetricTime(Long clusterId) {
        String appendQueryDsl = buildTermDsl("clusterPhyId" ,clusterId);
        return getLatestMetricTime(appendQueryDsl);
    }

    protected Long getLatestMetricTime(Long clusterId, String appendQueryDsl) {
        String clusterQueryDsl = buildTermDsl("clusterPhyId" ,clusterId);

        return getLatestMetricTime(appendQueryDsl + "," + clusterQueryDsl);
    }

    protected Long getLatestMetricTime(String appendQueryDsl) {
        Long endTime    = System.currentTimeMillis();
        Long startTime  = endTime - 12 * ONE_HOUR;

        String dsl = dslLoaderUtil.getFormatDslByFileName(DslsConstant.GET_LATEST_METRIC_TIME, startTime, endTime, appendQueryDsl);
        String realIndexName = IndexNameUtils.genDailyIndexName(indexName, startTime, endTime);

        return esOpClient.performRequest(realIndexName, dsl, s -> s.getHits().getHits().isEmpty()
                ? System.currentTimeMillis() : ((Map<String, Long>)s.getHits().getHits().get(0).getSource()).get(TIME_STAMP), 3);
    }
}
