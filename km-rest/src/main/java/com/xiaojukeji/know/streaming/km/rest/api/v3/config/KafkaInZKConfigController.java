package com.xiaojukeji.know.streaming.km.rest.api.v3.config;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka.KafkaInZKConfigChangedVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaConfigInZK-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaInZKConfigController {
    @ApiOperation(value = "Kafka-ZK上的配置变更记录", notes = "")
    @GetMapping(value = "clusters/{clusterPhyId}/in-zk/config-changed-history")
    @ResponseBody
    public PaginationResult<KafkaInZKConfigChangedVO> getKafkaInZKConfigChangedHistory(@PathVariable Long clusterPhyId,
                                                                                       PaginationBaseDTO dto) {
        return PaginationResult.buildSuc(dto);
    }
}
