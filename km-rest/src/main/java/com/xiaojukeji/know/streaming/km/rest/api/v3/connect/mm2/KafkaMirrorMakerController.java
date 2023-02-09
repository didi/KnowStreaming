package com.xiaojukeji.know.streaming.km.rest.api.v3.connect.mm2;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.biz.connect.mm2.MirrorMakerManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMaker2ActionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMakerCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.mm2.MirrorMaker2DeleteDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.vo.connect.plugin.ConnectConfigInfosVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectActionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zengqiao
 * @date 22/12/12
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "MM2-MM2自身-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_MM2_PREFIX)
public class KafkaMirrorMakerController {
    @Autowired
    private MirrorMakerManager mirrorMakerManager;

    @ApiOperation(value = "创建MM2", notes = "")
    @PostMapping(value = "mirror-makers")
    @ResponseBody
    public Result<Void> createMM2(@Validated @RequestBody MirrorMakerCreateDTO dto) {
        return mirrorMakerManager.createMirrorMaker(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "删除MM2", notes = "")
    @DeleteMapping(value ="mirror-makers")
    @ResponseBody
    public Result<Void> deleteMM2(@Validated @RequestBody MirrorMaker2DeleteDTO dto) {
        return mirrorMakerManager.deleteMirrorMaker(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "操作MM2", notes = "")
    @PutMapping(value ="mirror-makers")
    @ResponseBody
    public Result<Void> operateMM2s(@Validated @RequestBody MirrorMaker2ActionDTO dto) {
        if (ConnectActionEnum.RESTART.getValue().equals(dto.getAction())) {
            return mirrorMakerManager.restartMirrorMaker(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        } else if (ConnectActionEnum.STOP.getValue().equals(dto.getAction())) {
            return mirrorMakerManager.stopMirrorMaker(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        } else if (ConnectActionEnum.RESUME.getValue().equals(dto.getAction())) {
            return mirrorMakerManager.resumeMirrorMaker(dto.getConnectClusterId(), dto.getConnectorName(), HttpRequestUtil.getOperator());
        }

        return Result.buildFailure(ResultStatus.PARAM_ILLEGAL);
    }

    @ApiOperation(value = "MM2配置修改", notes = "")
    @PutMapping(value ="mirror-makers-config")
    @ResponseBody
    public Result<Void> modifyMM2s(@Validated @RequestBody MirrorMakerCreateDTO dto) {
        return mirrorMakerManager.modifyMirrorMakerConfig(dto, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "校验MM2配置", notes = "")
    @PutMapping(value ="mirror-makers-config/validate")
    @ResponseBody
    public Result<List<ConnectConfigInfosVO>> validateConnectors(@Validated @RequestBody MirrorMakerCreateDTO dto) {
        return mirrorMakerManager.validateConnectors(dto);
    }
}
