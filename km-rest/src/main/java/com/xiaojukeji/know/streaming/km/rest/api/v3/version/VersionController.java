package com.xiaojukeji.know.streaming.km.rest.api.v3.version;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.version.VersionControlManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.UserMetricConfigDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.metric.UserMetricConfigVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.version.VersionItemVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author zengqiao
 * @date 21/07/16
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "版本控制&兼容-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class VersionController {
    @Autowired
    private VersionControlManager versionControlManager;

    @ApiOperation(value = "KS支持的kafka版本列表", notes = "")
    @GetMapping(value = "support-kafka-versions")
    @ResponseBody
    public Result<SortedMap<String, Long>> listAllVersions() {
        Result<Map<String, Long>> rm = versionControlManager.listAllKafkaVersions();
        if (rm.failed()) {
            return Result.buildFromIgnoreData(rm);
        }

        return Result.buildSuc(new TreeMap<>(rm.getData()));
    }

    @ApiOperation(value = "支持的kafka-Connect版本列表", notes = "")
    @GetMapping(value = "support-kafka-connect-versions")
    @ResponseBody
    public Result<SortedMap<String, Long>> listAllConnectVersions() {
        Result<Map<String, Long>> rm = versionControlManager.listAllKafkaVersions();
        if (rm.failed()) {
            return Result.buildFromIgnoreData(rm);
        }

        return Result.buildSuc(new TreeMap<>(rm.getData()));
    }

    @ApiOperation(value = "查询当前所有的兼容性(指标、前端操作)配置信息", notes = "")
    @GetMapping(value = "kafka-versions-items")
    @ResponseBody
    public Result<Map<String, VersionItemVO>> listAllVersionItem(){
        return versionControlManager.listAllVersionItem();
    }

    @ApiOperation(value = "集群的版本兼容项，查询当前集群版本下支持的指标或操作", notes = "")
    @GetMapping(value = "clusters/{clusterId}/types/{type}/support-kafka-versions")
    @ResponseBody
    public Result<List<VersionItemVO>> listClusterVersionControlItem(@PathVariable Long clusterId, @PathVariable Integer type) {
        return versionControlManager.listKafkaClusterVersionControlItem(clusterId, type);
    }

    @ApiOperation(value = "用户设置的指标显示项", notes = "")
    @GetMapping(value = "clusters/{clusterId}/types/{type}/user-metric-config")
    @ResponseBody
    public Result<List<UserMetricConfigVO>> listUserMetricItem(@PathVariable Long clusterId, @PathVariable Integer type, HttpServletRequest request) {
        return versionControlManager.listUserMetricItem(clusterId, type, HttpRequestUtil.getOperator(request));
    }

    @ApiOperation(value = "用户设置指标显示项", notes = "")
    @PostMapping(value = "clusters/{clusterId}/types/{type}/user-metric-config")
    @ResponseBody
    public Result<Void> updateUserMetricItem(@PathVariable Long clusterId, @PathVariable Integer type,
                                             @Validated @RequestBody UserMetricConfigDTO userMetricConfigDTO, HttpServletRequest request) {
        return versionControlManager.updateUserMetricItem(clusterId, type, userMetricConfigDTO, HttpRequestUtil.getOperator(request));
    }
}
