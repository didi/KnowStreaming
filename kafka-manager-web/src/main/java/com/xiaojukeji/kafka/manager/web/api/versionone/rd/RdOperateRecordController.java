package com.xiaojukeji.kafka.manager.web.api.versionone.rd;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.rd.OperateRecordDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.OperateRecordVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import com.xiaojukeji.kafka.manager.web.converters.OperateRecordModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author zhongyuankai_i
 * @date 20/09/03
 */
@Api(tags = "RD-operate相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_RD_PREFIX)
public class RdOperateRecordController {
    private static final int MAX_RECORD_COUNT = 200;

    @Autowired
    private OperateRecordService operateRecordService;

    @ApiOperation(value = "查询操作记录", notes = "")
    @PostMapping(value = "operate-record")
    @ResponseBody
    public Result<List<OperateRecordVO>> geOperateRecords(@RequestBody OperateRecordDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        List<OperateRecordVO> voList = OperateRecordModelConverter.convert2OperateRecordVOList(operateRecordService.queryByCondition(dto));
        if (voList.size() > MAX_RECORD_COUNT) {
            voList = voList.subList(0, MAX_RECORD_COUNT);
        }
        return new Result<>(voList);
    }
}
