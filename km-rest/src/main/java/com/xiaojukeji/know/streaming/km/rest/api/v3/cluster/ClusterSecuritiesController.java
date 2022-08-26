package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationPreciseAndFuzzySearchDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationFuzzySearchFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaAclPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.KafkaUserPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.acl.AclBindingVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser.KafkaUserVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaAclConverter;
import com.xiaojukeji.know.streaming.km.common.converter.KafkaUserVOConverter;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.acl.KafkaAclService;
import com.xiaojukeji.know.streaming.km.core.service.kafkauser.KafkaUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/23
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群Security-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterSecuritiesController {
    @Autowired
    private KafkaAclService kafkaAclService;

    @Autowired
    private KafkaUserService kafkaUserService;

    @ApiOperation(value = "集群所有ACL信息")
    @PostMapping(value = "clusters/{clusterPhyId}/acl-bindings")
    @ResponseBody
    public PaginationResult<AclBindingVO> aclBindings(@PathVariable Long clusterPhyId,
                                                      @RequestBody @Validated PaginationPreciseAndFuzzySearchDTO dto) {
        List<KafkaAclPO> poList = kafkaAclService.getKafkaAclFromDB(clusterPhyId);

        List<AclBindingVO> voList = KafkaAclConverter.convert2AclBindingVOList(poList);

        // 精确搜索
        if (!ValidateUtils.isEmptyList(dto.getPreciseFilterDTOList())) {
            for (PaginationPreciseFilterFieldDTO preciseFilterFieldDTO: dto.getPreciseFilterDTOList()) {
                voList = PaginationUtil.pageByPreciseFilter(voList, preciseFilterFieldDTO.getFieldName(), preciseFilterFieldDTO.getFieldValueList());
            }
        }

        // 模糊搜索
        if (!ValidateUtils.isEmptyList(dto.getFuzzySearchDTOList())) {
            for (PaginationFuzzySearchFieldDTO fuzzySearchFieldDTO: dto.getFuzzySearchDTOList()) {
                voList = PaginationUtil.pageByFuzzyFilter(voList, fuzzySearchFieldDTO.getFieldValue(), Arrays.asList(fuzzySearchFieldDTO.getFieldName()));
            }
        }

        // 分页
        return PaginationUtil.pageBySubData(voList, dto);
    }

    @ApiOperation(value = "集群所有Kafka-User信息(不分页)")
    @GetMapping(value = "clusters/{clusterPhyId}/kafka-users")
    @ResponseBody
    public Result<List<KafkaUserVO>> getKafkaUsers(@PathVariable Long clusterPhyId, @RequestParam(required = false) String searchKeyword) {
        List<KafkaUserPO> poList = kafkaUserService.getKafkaUserByClusterIdFromDB(clusterPhyId, searchKeyword);
        return Result.buildSuc(KafkaUserVOConverter.convert2KafkaUserVOList(clusterPhyId, poList));
    }

    @ApiOperation(value = "集群所有Kafka-User信息(分页)")
    @PostMapping(value = "clusters/{clusterPhyId}/kafka-users")
    @ResponseBody
    public PaginationResult<KafkaUserVO> getKafkaUsers(@PathVariable Long clusterPhyId, @RequestBody @Validated PaginationBaseDTO dto) {
        PaginationResult<KafkaUserPO> paginationResult = kafkaUserService.pagingKafkaUserFromDB(clusterPhyId, dto);
        if (paginationResult.failed()) {
            return PaginationResult.buildFailure(paginationResult, dto);
        }

        return PaginationResult.buildSuc(KafkaUserVOConverter.convert2KafkaUserVOList(clusterPhyId, paginationResult.getData().getBizData()), paginationResult);
    }
}
