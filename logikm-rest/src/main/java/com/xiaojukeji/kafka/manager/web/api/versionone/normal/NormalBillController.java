package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.BillStaffDetailVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.BillStaffSummaryVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.BillTopicVO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.KafkaBillService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/4/26
 */
@Api(tags = "Normal-Bill相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalBillController {
    @Autowired
    private KafkaBillService kafkaBillService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @ApiOperation(value = "用户账单概览", notes = "")
    @RequestMapping(value = "bills/staff-summary", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<BillStaffSummaryVO>> getBillStaffSummary(@RequestParam("startTime") Long startTime,
                                                                @RequestParam("endTime") Long endTime) {
        List<KafkaBillDO> kafkaBillDOList =
                kafkaBillService.getByPrincipal(SpringTool.getUserName(), new Date(startTime), new Date(endTime));
        if (ValidateUtils.isEmptyList(kafkaBillDOList)) {
            return new Result<>();
        }

        Map<String, BillStaffSummaryVO> billMap = new TreeMap<>();
        for (KafkaBillDO kafkaBillDO: kafkaBillDOList) {
            BillStaffSummaryVO vo = billMap.get(kafkaBillDO.getGmtDay());
            if (ValidateUtils.isNull(vo)) {
                vo = new BillStaffSummaryVO();
                vo.setUsername(SpringTool.getUserName());
                vo.setTopicNum(0);
                vo.setQuota(0.0);
                vo.setCost(0.0);
                vo.setGmtMonth(kafkaBillDO.getGmtDay());
                vo.setTimestamp(kafkaBillDO.getGmtCreate().getTime());
                billMap.put(kafkaBillDO.getGmtDay(), vo);
            }
            vo.setTopicNum(vo.getTopicNum() + 1);
            vo.setQuota(vo.getQuota() + kafkaBillDO.getQuota());
            vo.setCost(vo.getCost() + kafkaBillDO.getCost());
        }
        return new Result<>(new ArrayList<>(billMap.values()));
    }

    @ApiOperation(value = "用户账单详情", notes = "")
    @RequestMapping(value = "bills/staff-detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<BillStaffDetailVO> getBillStaffDetail(@RequestParam("timestamp") Long timestamp) {
        List<KafkaBillDO> kafkaBillDOList =
                kafkaBillService.getByGmtDay(DateUtils.getFormattedDate(timestamp).substring(0, 7));
        if (ValidateUtils.isEmptyList(kafkaBillDOList)) {
            return new Result<>();
        }
        String username = SpringTool.getUserName();

        BillStaffDetailVO billStaffDetailVO = new BillStaffDetailVO();
        billStaffDetailVO.setUsername(username);
        billStaffDetailVO.setBillList(new ArrayList<>());

        Double costSum = 0.0;
        for (KafkaBillDO kafkaBillDO: kafkaBillDOList) {
            if (!kafkaBillDO.getPrincipal().equals(username)) {
                continue;
            }
            BillTopicVO vo = new BillTopicVO();
            vo.setClusterName("unknown");
            LogicalClusterDO logicalClusterDO = logicalClusterMetadataManager.getTopicLogicalCluster(
                    kafkaBillDO.getClusterId(),
                    kafkaBillDO.getTopicName()
            );
            if (!ValidateUtils.isNull(logicalClusterDO)) {
                vo.setClusterId(logicalClusterDO.getId());
                vo.setClusterName(logicalClusterDO.getName());
            }
            vo.setTopicName(kafkaBillDO.getTopicName());
            vo.setQuota(kafkaBillDO.getQuota());
            vo.setCost(kafkaBillDO.getCost());
            costSum += kafkaBillDO.getCost();
            billStaffDetailVO.getBillList().add(vo);
        }
        billStaffDetailVO.setCostSum(costSum);
        return new Result<>(billStaffDetailVO);
    }
}