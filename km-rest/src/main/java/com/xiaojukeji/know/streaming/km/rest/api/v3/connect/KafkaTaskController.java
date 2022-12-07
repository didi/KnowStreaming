package com.xiaojukeji.know.streaming.km.rest.api.v3.connect;


import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.task.TaskActionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.connect.worker.WorkerConnectorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/10/17
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "Connect-Task-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_CONNECT_PREFIX)
public class KafkaTaskController {

    @Autowired
    private WorkerConnectorService workerConnectorService;

    @ApiOperation(value = "操作Task", notes = "")
    @PutMapping(value ="tasks")
    @ResponseBody
    public Result<Void> actionTask(@Validated @RequestBody TaskActionDTO dto) {
        return workerConnectorService.actionTask(dto);
    }
}
