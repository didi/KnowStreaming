package com.xiaojukeji.know.streaming.km.license.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.component.RestTool;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.NetUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.km.KmNodeService;
import com.xiaojukeji.know.streaming.km.license.service.LicenseService;
import com.xiaojukeji.know.streaming.km.license.bean.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LicenseServiceImpl implements LicenseService {
    private static final ILog LOGGER = LogFactory.getLog(LicenseServiceImpl.class);

    private static final String LICENSE_INFO_URL  = "/api/license/info";
    private static final String LICENSE_USAGE_URL = "/api/license/usage";

    private static final String LICENSE_HEADER_TOKEN        = "x-l-token";
    private static final String LICENSE_HEADER_APP          = "x-l-app-name";
    private static final String LICENSE_HEADER_SIGNATURE    = "x-l-signature";

    private static final int FAILED_NO_LICENSE              = 1000000000;
    private static final int FAILED_LICENSE_EXPIRE          = 1000000001;
    private static final int FAILED_LICENSE_CLUSTER_LIMIT   = 1000000002;

    private static final int ONE_HOUR               = 60 * 60 * 1000;

    @Value("${license.server}")
    private String licenseSrvUrl;

    @Value("${license.signature}")
    private String licenseSignature;

    @Value("${license.token}")
    private String licenseToken;

    @Value("${license.app-name}")
    private String appName;

    @Autowired
    private KmNodeService kmNodeService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private RestTool restTool;

    private LicenseInfo<KmLicense> kmLicense;

    private List<LicenseUsage> licenseUsages = new ArrayList<>();

    @Override
    public Result<Void> addClusterLimit() {
        //对 LicenseUsage 按照时间挫，从小到大排序，即最新的在最后面
        licenseUsages.sort((o1, o2) -> o1.getTimeStamp() < o2.getTimeStamp() ? 1 : -1);

        List<KmLicenseUsageDetail> details = licenseUsages.stream()
                .map(l -> JSON.parseObject(l.getData(), KmLicenseUsageDetail.class))
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(details)){return Result.buildSuc();}

        //Tuple.v1 : ks cluster hosts
        //Tuple.v2 : ks 集群管理的 kafka 集群个数
        List<Tuple<List<String>, Integer>> ksClusterHostsList = new ArrayList<>();
        ksClusterHostsList.add(new Tuple<>(details.get(0).getHosts(), details.get(0).getClusters()));

        //根据 hosts 是否有交集，来获取 ks 的集群列表
        for(KmLicenseUsageDetail detail : details){
            for(Tuple<List<String>, Integer> tuple : ksClusterHostsList){
                if(isListIntersection(tuple.getV1(), detail.getHosts())){
                    tuple.setV1(detail.getHosts());
                    tuple.setV2(detail.getClusters());
                }else {
                    ksClusterHostsList.add(new Tuple<>(detail.getHosts(), detail.getClusters()));
                }
            }
        }

        LOGGER.debug("method=addClusterLimit||details={}||ksClusterHostsList={}",
                JSON.toJSONString(details), JSON.toJSONString(ksClusterHostsList));

        //计算索引 ks 集群管理的 kafka 集群总个数
        final int[] totalKafkaClusterNus = {0};
        ksClusterHostsList.stream().forEach(l -> totalKafkaClusterNus[0] += l.getV2() );

        if(null == kmLicense) {
            return Result.buildFailure(FAILED_NO_LICENSE, "无法获取KS的License信息");
        }

        if(kmLicense.getInfo().getClusters() < totalKafkaClusterNus[0]) {
            return Result.buildFailure(FAILED_LICENSE_CLUSTER_LIMIT, String.format("KS管理的Kafka集群已达到License限制的%d个集群", kmLicense.getInfo().getClusters()));
        }

        return Result.buildSuc();
    }

    /**
     * 当前这个接口只做最小限度的校验，即 km-license 模块和 license 信息存在，
     * 其他异常情况，如：license-srv 临时挂掉不考虑
     * check 接口返回的异常 code、msg，就在该模块定义，不要放到 ResultStatus 中
     */
    @Override
    public Result<Void> check() {
        if(null == kmLicense){
            return Result.buildFailure(FAILED_NO_LICENSE, "无法获取KS的license信息");
        }

        if(System.currentTimeMillis() > kmLicense.getExpiredDate() * 1000){
            return Result.buildFailure(FAILED_LICENSE_EXPIRE, "当前KS的license已过期");
        }

        return Result.buildSuc();
    }

    @PostConstruct
    public void init(){
        syncLicenseInfo();
    }

    /**
     * 每10分钟同步一次
     */
    @Scheduled(cron="0 0/10 * * * ?")
    public void syncLicenseInfo(){
        try {
            saveLicenseUsageInfo();

            List<LicenseUsage> licenseUsages = listLicenseUsageInfo();
            if(!CollectionUtils.isEmpty(licenseUsages)){
                this.licenseUsages.clear();
                this.licenseUsages.addAll(licenseUsages);
            }

            LicenseInfo<KmLicense> kmLicense = this.getLicenseInfo();
            if(null != kmLicense){
                this.kmLicense = kmLicense;
            }
        } catch (Exception e){
            LOGGER.error("method=syncLicenseInfo||msg=exception!", e);
        }
    }

    /**************************************************** private method ****************************************************/

    private LicenseInfo<KmLicense> getLicenseInfo(){
        String url = licenseSrvUrl + LICENSE_INFO_URL;
        LicenseResult<String> ret = restTool.getForObject(
                url, genHeaders(), new TypeReference<LicenseResult<String>>(){});

        LOGGER.debug("method=getLicenseInfo||url={}||ret={}", url, JSON.toJSONString(ret));

        if(!StringUtils.isEmpty(ret.getErr())){
            return null;
        }

        byte[] encrypted = Base64.getDecoder().decode(ret.getReply().getBytes(StandardCharsets.UTF_8));
        LicenseInfo<KmLicense> info = JSON.parseObject(
                new String(encrypted),
                new TypeReference<LicenseInfo<KmLicense>>(){}
                );

        return info;
    }

    private List<LicenseUsage> listLicenseUsageInfo(){
        String url = licenseSrvUrl + LICENSE_USAGE_URL;
        LicenseResult<List<LicenseUsage>> ret = restTool.getForObject(
                url, genHeaders(), new TypeReference<LicenseResult<List<LicenseUsage>>>(){});

        LOGGER.debug("method=listLicenseUsageInfo||url={}||ret={}", url, JSON.toJSONString(ret));

        if(!StringUtils.isEmpty(ret.getErr())){
            return new ArrayList<>();
        }

        List<LicenseUsage> licenseUsages = ret.getReply();
        if(!CollectionUtils.isEmpty(licenseUsages)){
            long now = System.currentTimeMillis();

            return licenseUsages.stream()
                    .filter(l -> l.getTimeStamp() + 6 * ONE_HOUR > now)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private boolean saveLicenseUsageInfo(){
        String host = NetUtils.localHost();

        KmLicenseUsageDetail detail = new KmLicenseUsageDetail();
        detail.setHost(host);
        detail.setHosts(kmNodeService.listKmHosts());
        detail.setClusters(clusterPhyService.listAllClusters().size());

        LicenseUsage licenseUsage = new LicenseUsage();
        licenseUsage.setTimeStamp(System.currentTimeMillis());
        licenseUsage.setUuid(CommonUtils.getMD5(host));
        licenseUsage.setData(JSON.toJSONString(detail));

        Map<String, String> param = new HashMap<>();
        param.put("usageSecret", Base64.getEncoder().encodeToString(JSON.toJSONString(licenseUsage).getBytes(StandardCharsets.UTF_8)));

        String url = licenseSrvUrl + LICENSE_USAGE_URL;
        LicenseResult<Void> ret = restTool.putForObject(url, genHeaders(), JSON.toJSONString(param), LicenseResult.class);

        LOGGER.debug("method=saveLicenseUsageInfo||url={}||ret={}", url, JSON.toJSONString(ret));

        if(!StringUtils.isEmpty(ret.getErr())){
            return false;
        }

        return true;
    }

    private HttpHeaders genHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(LICENSE_HEADER_TOKEN, licenseToken);
        headers.add(LICENSE_HEADER_APP, appName);
        headers.add(LICENSE_HEADER_SIGNATURE, licenseSignature);
        headers.add("content-type", "application/json");
        return headers;
    }

    /**
     * 两个 list 是否相交，是否有相同的内容
     * @return
     */
    private boolean isListIntersection(List<String> l, List<String> r){
        l.retainAll(r);
        return !CollectionUtils.isEmpty(l);
    }
}
