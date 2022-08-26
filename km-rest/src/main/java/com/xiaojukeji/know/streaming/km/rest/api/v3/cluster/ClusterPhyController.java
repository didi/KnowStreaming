package com.xiaojukeji.know.streaming.km.rest.api.v3.cluster;

import com.didiglobal.logi.security.util.HttpRequestUtil;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterPhyAddDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterPhyModifyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseVO;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ClusterConverter;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author zengqiao
 * @date 22/02/21
 */
@Api(tags = Constant.SWAGGER_API_TAG_PREFIX + "集群单Phy-相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V3_PREFIX)
public class ClusterPhyController {
    @Autowired
    private ClusterPhyService clusterPhyService;

    @ApiOperation(value = "接入物理集群", notes = "")
    @PostMapping(value = "physical-clusters")
    @ResponseBody
    public Result<Long> addClusterPhy(@Validated @RequestBody ClusterPhyAddDTO dto) throws Exception {
        return Result.buildSuc(
                clusterPhyService.addClusterPhy(ClusterConverter.convert2ClusterPhyPO(dto), HttpRequestUtil.getOperator())
        );
    }

    @ApiOperation(value = "删除物理集群")
    @DeleteMapping(value = "physical-clusters")
    @ResponseBody
    public Result<Void> deleteClusterPhy(@RequestParam("clusterPhyId") Long clusterPhyId) {
        return clusterPhyService.removeClusterPhyById(clusterPhyId, HttpRequestUtil.getOperator());
    }

    @ApiOperation(value = "修改物理集群", notes = "")
    @PutMapping(value = "physical-clusters")
    @ResponseBody
    public Result<Void> modifyClusterPhyById(@Validated @RequestBody ClusterPhyModifyDTO dto) throws Exception {
        clusterPhyService.modifyClusterPhyById(ClusterConverter.convert2ClusterPhyPO(dto), HttpRequestUtil.getOperator());

        return Result.buildSuc();
    }

    @ApiOperation(value = "物理集群基本信息", notes = "")
    @GetMapping(value = "physical-clusters/{clusterPhyId}/basic")
    @ResponseBody
    public Result<ClusterPhyBaseVO> getClusterPhyBasic(@PathVariable Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(clusterPhy, ClusterPhyBaseVO.class));
    }

    @ApiOperation(value = "物理集群基本信息", notes = "")
    @GetMapping(value = "physical-clusters/{clusterPhyName}/basic-combine-exist")
    @ResponseBody
    public Result<ClusterPhyBaseCombineExistVO> getClusterPhyBasicCombineExist(@PathVariable String clusterPhyName) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByClusterName(clusterPhyName);
        if (clusterPhy == null) {
            ClusterPhyBaseCombineExistVO vo = new ClusterPhyBaseCombineExistVO();
            vo.setExist(false);
            return Result.buildSuc(vo);
        }

        ClusterPhyBaseCombineExistVO vo = ConvertUtil.obj2Obj(clusterPhy, ClusterPhyBaseCombineExistVO.class);
        vo.setExist(true);
        return Result.buildSuc(vo);
    }
}
