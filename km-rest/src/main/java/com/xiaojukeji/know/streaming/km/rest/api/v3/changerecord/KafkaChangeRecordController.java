package com.xiaojukeji.know.streaming.km.rest.api.v3.changerecord;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.changerecord.KafkaChangeRecordVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.ChangeRecordVOConverter;
import com.xiaojukeji.know.streaming.km.core.service.change.record.KafkaChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/04/07
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Change记录-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaChangeRecordController {
    @Autowired
    private KafkaChangeRecordService kafkaChangeRecordService;

    @ApiOperation(value = "集群变更记录")
    @GetMapping(value = "clusters/{clusterPhyId}/change-records")
    @ResponseBody
    public PaginationResult<KafkaChangeRecordVO> getClusterPhyChangeRecords(@PathVariable Long clusterPhyId, PaginationBaseDTO dto) {
        IPage<KafkaChangeRecordPO> iPage = kafkaChangeRecordService.pagingByCluster(clusterPhyId, dto);
        return PaginationResult.buildSuc(
                ChangeRecordVOConverter.convert2KafkaChangeRecordVOList(iPage.getRecords()),
                iPage
        );
    }
}
