package com.xiaojukeji.know.streaming.km.rest.api.v3.connect;


import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.connect.connector.ConnectorManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorActionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.connector.ConnectorDeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.config.ConnectConfigInfos;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectConfigInfosVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectActionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.connect.connector.OpConnectorService;
import com.xiaojukeji.know.streaming.km.core.service.connect.plugin.PluginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Connect-Connector自身-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_CONNECT_PREFIX)
public class KafkaConnectorController {
    @Autowired
    private OpConnectorService opConnectorService;

    @Autowired
    private ConnectorManager connectorManager;

    @Autowired
    private PluginService pluginService;

    @ApiOperation(value = "创建Connector", notes = "")
    @PostMapping(value = "connectors")
    @ResponseBody
    public Result<Void> createConnector(@Validated @RequestBody ConnectorCreateDTO dto) {
        if (ValidateUtils.isNull(dto.getSuitableConfig())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "config字段不能为空");
        }

        return connectorManager.createConnector(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "删除Connector", notes = "")
    @DeleteMapping(value ="connectors")
    @ResponseBody
    public Result<Void> deleteConnectors(@Validated @RequestBody ConnectorDeleteDTO dto) {
        return opConnectorService.deleteConnector(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "操作Connector", notes = "")
    @PutMapping(value ="connectors")
    @ResponseBody
    public Result<Void> operateConnectors(@Validated @RequestBody ConnectorActionDTO dto) {
        if (ConnectActionEnum.RESTART.getValue().equals(dto.getAction())) {
            return opConnectorService.restartConnector(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        } else if (ConnectActionEnum.STOP.getValue().equals(dto.getAction())) {
            return opConnectorService.stopConnector(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        } else if (ConnectActionEnum.RESUME.getValue().equals(dto.getAction())) {
            return opConnectorService.resumeConnector(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        }

        return Result.buildFailure(ResultStatus.PARAM_ILLEGAL);
    }

    @ApiOperation(value = "修改Connector配置", notes = "")
    @PutMapping(value ="connectors-config")
    @ResponseBody
    public Result<Void> modifyConnectors(@Validated @RequestBody ConnectorCreateDTO dto) {
        if (ValidateUtils.isNull(dto.getSuitableConfig())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "config字段不能为空");
        }

        return connectorManager.updateConnectorConfig(
                dto.getConnectClusterId(),
                dto.getConnectorName(),
                dto.getSuitableConfig(),
                HttpRequestUtil.getOperator()
        );
    }

    @ApiOperation(value = "校验Connector配置", notes = "")
    @PutMapping(value ="connectors-config/validate")
    @ResponseBody
    public Result<ConnectConfigInfosVO> validateConnectors(@Validated @RequestBody ConnectorCreateDTO dto) {
        if (ValidateUtils.isNull(dto.getSuitableConfig())) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "config字段不能为空");
        }

        Result<ConnectConfigInfos> infoResult = pluginService.validateConfig(dto.getConnectClusterId(), dto.getSuitableConfig());
        if (infoResult.failed()) {
            return Result.buildFromIgnoreData(infoResult);
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(infoResult.getData(), ConnectConfigInfosVO.class));
    }
}
