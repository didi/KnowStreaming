package com.xiaojukeji.know.streaming.km.rest.api.v3.connect;


import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.plugin.ConnectPluginBasic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectConfigInfosVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectPluginBasicVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.connect.plugin.PluginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Connect-Plugin-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_CONNECT_PREFIX)
public class KafkaConnectPluginController {

    @Autowired
    private PluginService pluginService;

    @ApiOperation(value = "Connect集群插件", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connector-plugins")
    @ResponseBody
    public Result<List<ConnectPluginBasicVO>> getConnectorPlugins(@PathVariable Long connectClusterId) {
        Result<List<ConnectPluginBasic>> listResult = pluginService.listPluginsFromCluster(connectClusterId);
        if (listResult.failed()) {
            return Result.buildFromIgnoreData(listResult);
        }

        listResult.getData().forEach(elem -> elem.setHelpDocLink("https://www.confluent.io/hub/"));
        return Result.buildSuc(ConvertUtil.list2List(listResult.getData(), ConnectPluginBasicVO.class));
    }

    @ApiOperation(value = "Connect插件配置", notes = "")
    @GetMapping(value = "clusters/{connectClusterId}/connector-plugins/{pluginName}/config")
    @ResponseBody
    public Result<ConnectConfigInfosVO> getPluginConfig(@PathVariable Long connectClusterId, @PathVariable String pluginName) {
        Result<ConnectConfigInfos> infosResult = pluginService.getConfig(connectClusterId, pluginName);
        if (infosResult.failed()) {
            return Result.buildFromIgnoreData(infosResult);
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(infosResult.getData(), ConnectConfigInfosVO.class));
    }
}
