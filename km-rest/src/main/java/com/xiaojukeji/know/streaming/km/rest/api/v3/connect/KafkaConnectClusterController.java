package com.xiaojukeji.know.streaming.km.rest.api.v3.connect;


import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.cluster.ConnectClusterDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.connect.cluster.ConnectClusterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Connect-Cluster-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_CONNECT_PREFIX)
public class KafkaConnectClusterController {
    @Autowired
    private ConnectClusterService connectClusterService;

    @ApiOperation(value = "删除Connect集群")
    @DeleteMapping(value = "connect-clusters")
    @ResponseBody
    public Result<Void> deleteConnectCluster(@RequestParam("connectClusterId") Long connectClusterId) {
        return connectClusterService.deleteInDB(connectClusterId, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "修改Connect集群", notes = "")
    @PutMapping(value = "batch-connect-clusters")
    @ResponseBody
    public Result<Void> batchModifyConnectCluster(@Validated @RequestBody List<ConnectClusterDTO> dtoList) {
        return connectClusterService.batchModifyInDB(dtoList, HttpRequestUtil.getOperator());
    }
}
