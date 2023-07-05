package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.Maps;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BaseMetricESPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.IndexNameUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.persistence.es.BaseESDAO;
import com.xiaojukeji.know.streaming.km.persistence.es.ESTPService;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateLoaderUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.IntStream;

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

    private static final int        INDEX_DAYS = 7;

    /**
     * 不同维度 kafka 监控数据
     */
    private static Map<String, BaseMetricESDAO> ariusStatsEsDaoMap    = Maps.newConcurrentMap();

    @Autowired
    private TemplateLoaderUtil templateLoaderUtil;

    @Autowired
    protected ESTPService esTPService;

    /**
     * es 地址
     */
    @Value("${es.index.expire:60}")
    private int indexExpireDays;

    /**
     * 检查 es 索引是否存在，不存在则创建索引
     */
    @Scheduled(cron = "0 3/5 * * * ?")
    public void checkCurrentDayIndexExist(){
        try {
            String indexTemplate = templateLoaderUtil.getContextByFileName(indexName);
            esOpClient.createIndexTemplateIfNotExist(indexName, indexTemplate);

            int retainDays = indexExpireDays > INDEX_DAYS ? INDEX_DAYS : indexExpireDays;
            // 检查最近【retainDays】天索引存在不存
            IntStream.range(0, retainDays).mapToObj(i -> IndexNameUtils.genDailyIndexName(indexName, i))
                    .filter(realIndex -> !esOpClient.indexExist(realIndex))
                    .forEach(realIndex -> esOpClient.createIndex(realIndex));
        } catch (Exception e) {
            LOGGER.error("method=checkCurrentDayIndexExist||errMsg=exception!", e);
        }
    }

    @Scheduled(cron = "0 30/45 * * * ?")
    public void delExpireIndex(){
        List<String> indexList = esOpClient.listIndexByName(indexName);
        if(CollectionUtils.isEmpty(indexList)){return;}

        indexList.sort((o1, o2) -> -o1.compareTo(o2));

        int size = indexList.size();
        if(size > indexExpireDays){
            if(!EnvUtil.isOnline()){
                LOGGER.info("method=delExpireIndex||indexExpireDays={}||delIndex={}",
                        indexExpireDays, indexList.subList(indexExpireDays, size));
            }

            indexList.subList(indexExpireDays, size).forEach(s -> esOpClient.delIndexByName(s));
        }
    }

    public static BaseMetricESDAO getByStatsType(String statsType) {
        return ariusStatsEsDaoMap.get(statsType);
    }

    /**
     * 注册不同维度数据对应操作的es类
     *
     * @param statsType
     * @param baseAriusStatsEsDao
     */
    public static void register(String statsType, BaseMetricESDAO baseAriusStatsEsDao) {
        ariusStatsEsDaoMap.put(statsType, baseAriusStatsEsDao);
    }

    /**
     * 注册不同维度数据对应操作的es类
     *
     * @param baseAriusStatsEsDao
     */
    public void register(BaseMetricESDAO baseAriusStatsEsDao) {
        BaseMetricESDAO.register(indexName, baseAriusStatsEsDao);
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

    protected Map<String, ESAggr> checkBucketsAndHitsOfResponseAggs(ESQueryResponse response) {
        if(null == response
                || null == response.getAggs()
                || null == response.getAggs().getEsAggrMap()
                || null == response.getAggs().getEsAggrMap().get(HIST)
                || ValidateUtils.isEmptyList(response.getAggs().getEsAggrMap().get(HIST).getBucketList())) {
            return null;
        }

        return response.getAggs().getEsAggrMap();
    }

    protected int handleESQueryResponseCount(ESQueryResponse response){
        if(null == response || null == response.getHits()
                || null ==response.getHits().getUnusedMap()){return -1;}

        try {
            String total = response.getHits().getUnusedMap().get(TOTAL).toString();
            if(CommonUtils.isNumeric(total)){
                return Integer.valueOf(total);
            }else {
                return JSON.parseObject(total).getIntValue(VALUE);
            }
        }catch (Exception e){
            LOGGER.error("method=handleESQueryResponseCount||errMsg=exception!", e);
        }
        return 0;
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

        String dsl = dslLoaderUtil.getFormatDslByFileName( DslConstant.GET_LATEST_METRIC_TIME, startTime, endTime, appendQueryDsl);
        String realIndexName = IndexNameUtils.genDailyIndexName(indexName, startTime, endTime);

        return esOpClient.performRequest(
                realIndexName,
                dsl,
                s -> s == null || s.getHits().getHits().isEmpty() ? System.currentTimeMillis() : ((Map<String, Long>)s.getHits().getHits().get(0).getSource()).get(TIME_STAMP),
                3
        );
    }

    /**
     * 对 metricPointVOS 进行缺点优化
     */
    protected List<MetricPointVO> optimizeMetricPoints(List<MetricPointVO> metricPointVOS){
//        // 内部测试环境，不进行优化，直接返回
//        return metricPointVOS;

        // 开源环境，进行指标点优化
        if(CollectionUtils.isEmpty(metricPointVOS)){return metricPointVOS;}

        int size = metricPointVOS.size();
        if(size < 2){return metricPointVOS;}

        Collections.sort(metricPointVOS);

        List<MetricPointVO> rets = new ArrayList<>();
        for(int first = 0, second = first + 1; second < size; first++, second++){
            MetricPointVO firstPoint  = metricPointVOS.get(first);
            MetricPointVO secondPoint = metricPointVOS.get(second);

            if(null != firstPoint && null != secondPoint){
                rets.add(firstPoint);

                //说明有空点，那就增加一个点
                if(secondPoint.getTimeStamp() - firstPoint.getTimeStamp() > ONE_MIN){
                    MetricPointVO addPoint = new MetricPointVO();
                    addPoint.setName(firstPoint.getName());
                    addPoint.setAggType(firstPoint.getAggType());
                    addPoint.setValue(firstPoint.getValue());
                    addPoint.setTimeStamp(firstPoint.getTimeStamp() + ONE_MIN);

                    rets.add(addPoint);
                }

                if(second == size - 1){
                    rets.add(secondPoint);
                }
            }
        }

        return rets;
    }
}
