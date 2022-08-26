package com.xiaojukeji.know.streaming.km.rest.api.v3.kafkacontroller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.po.kafkacontrollr.KafkaControllerPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkacontroller.KafkaControllerVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.kafkacontroller.KafkaControllerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "KafkaController-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class KafkaController {
    @Autowired
    private KafkaControllerService kafkaControllerService;

    @ApiOperation(value = "KafkaController变更历史")
    @GetMapping(value = "clusters/{clusterPhyId}/controller-history")
    @ResponseBody
    public PaginationResult<KafkaControllerVO> getKafkaControllerHistory(@PathVariable Long clusterPhyId, PaginationBaseDTO dto) {
        IPage<KafkaControllerPO> iPage = kafkaControllerService.pagingControllerHistories(clusterPhyId, dto.getPageNo(), dto.getPageSize(), dto.getSearchKeywords());

        return PaginationResult.buildSuc(
                ConvertUtil.list2List(iPage.getRecords(), KafkaControllerVO.class),
                iPage
        );
    }
}
