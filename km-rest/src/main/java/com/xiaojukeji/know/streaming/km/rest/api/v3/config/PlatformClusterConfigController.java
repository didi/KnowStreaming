package com.xiaojukeji.know.streaming.km.rest.api.v3.config;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.bean.dto.config.platform.PlatformClusterConfigDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.platform.PlatformClusterConfigVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.config.PlatformClusterConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "PlatformClusterConfig-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class PlatformClusterConfigController {
    @Autowired
    private PlatformClusterConfigService platformClusterConfigService;

    @ApiOperation(value = "平台集群配置-查看", notes = "")
    @GetMapping(value = "platform-configs/clusters/{clusterId}/groups/{groupName}/configs")
    @ResponseBody
    public Result<List<PlatformClusterConfigVO>> getPlatformClusterConfig(@PathVariable Long clusterId, @PathVariable String groupName) {
        return Result.buildSuc(ConvertUtil.list2List(
                new ArrayList<>(platformClusterConfigService.getByClusterAndGroupWithoutDefault(clusterId, groupName).values()),
                PlatformClusterConfigVO.class)
        );
    }

    @ApiOperation(value = "平台集群配置-替换", notes = "")
    @PutMapping(value = "platform-configs")
    @ResponseBody
    public Result<Void> putPlatformClusterConfig(@Validated @RequestBody List<PlatformClusterConfigDTO> dtoList) {
        List<PlatformClusterConfigPO> poList = ConvertUtil.list2List(dtoList, PlatformClusterConfigPO.class);
        poList.stream().forEach(elem -> elem.setOperator(HttpRequestUtil.getOperator()));

        return platformClusterConfigService.batchReplace(poList, HttpRequestUtil.getOperator());
    }
}
