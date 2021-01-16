package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.common.annotations.ApiLevel;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.constant.ApiLevelContent;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.AppTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.QuotaVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppTopicAuthorityVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppTopicVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic.TopicConnectionVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppVO;
import com.xiaojukeji.kafka.manager.web.converters.AppConverter;
import com.xiaojukeji.kafka.manager.web.converters.TopicModelConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/7
 */
@Api(tags = "Normal-APP相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalAppController {
    @Autowired
    private AppService appService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private TopicConnectionService connectionService;

    @Autowired
    private AuthorityService authorityService;

    @ApiLevel(level = ApiLevelContent.LEVEL_NORMAL_3, rateLimit = 1)
    @ApiOperation(value = "App列表", notes = "")
    @RequestMapping(value = "apps", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AppVO>> getApps() {
        return new Result<>(AppConverter.convert2AppVOList(
                appService.getByPrincipal(SpringTool.getUserName()))
        );
    }

    @ApiOperation(value = "App基本信息", notes = "")
    @RequestMapping(value = "apps/{appId}/basic-info", method = RequestMethod.GET)
    @ResponseBody
    public Result<AppVO> getAppBasicInfo(@PathVariable String appId) {
        if (accountService.isOpOrRd(SpringTool.getUserName())) {
            return new Result<>(AppConverter.convert2AppVO(appService.getByAppId(appId)));
        }

        AppDO appDO = appService.getAppByUserAndId(appId, SpringTool.getUserName());
        if (appDO == null) {
            return Result.buildFrom(ResultStatus.USER_WITHOUT_AUTHORITY);
        }

        return new Result<>(AppConverter.convert2AppVO(appDO));
    }

    @ApiOperation(value = "App修改", notes = "")
    @RequestMapping(value = "apps", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyApp(@RequestBody AppDTO dto) {
        return Result.buildFrom(
                appService.updateByAppId(dto, SpringTool.getUserName(), false)
        );
    }

    @ApiOperation(value = "有权限的Topic信息", notes = "null: 全部, true:我的Topic, false:非我的有权限的Topic")
    @RequestMapping(value = "apps/{appId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<AppTopicVO>> getAppTopics(@PathVariable String appId,
                                                 @RequestParam(value = "mine", required = false) Boolean mine) {
        List<AppTopicDTO> dtoList = appService.getAppTopicDTOList(appId, mine);

        List<AppTopicVO> voList = new ArrayList<>();
        for (AppTopicDTO dto : dtoList) {
            if (TopicAuthorityEnum.DENY.getCode().equals(dto.getAccess())) {
                continue;
            }
            AppTopicVO vo = new AppTopicVO();
            CopyUtils.copyProperties(vo, dto);
            vo.setClusterId(dto.getLogicalClusterId());
            vo.setClusterName(dto.getLogicalClusterName());
            voList.add(vo);
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "Quota查询", notes = "")
    @RequestMapping(value = "apps/{appId}/quotas", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<QuotaVO>> getAppIdQuota(
            @PathVariable String appId,
            @RequestParam(value = "clusterId") Long clusterId,
            @RequestParam(value = "topicName") String topicName,
            @RequestParam(value = "isPhysicalClusterId", required = false) Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        return new Result<>(AppConverter.convert2QuotaVOList(
                clusterId, quotaService.getQuotaFromZk(physicalClusterId, topicName, appId))
        );
    }

    @ApiOperation(value = "应用连接信息", notes = "")
    @RequestMapping(value = "apps/{appId}/connections", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<TopicConnectionVO>> getAppIdQuota(@PathVariable String appId) {
        return new Result<>(TopicModelConverter.convert2TopicConnectionVOList(
                connectionService.getByAppId(
                        appId,
                        new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                        new Date()))
        );
    }

    @ApiOperation(value = "app对Topic权限信息", notes = "")
    @RequestMapping(value = "apps/{appId}/authorities", method = RequestMethod.GET)
    @ResponseBody
    public Result<AppTopicAuthorityVO> getAppIdQuota(@PathVariable String appId,
                                                     @RequestParam(value = "clusterId") Long clusterId,
                                                     @RequestParam(value = "topicName") String topicName) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, topicName)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        return new Result(AppConverter.convert2AppTopicAuthorityVO(
                appId,
                topicName,
                authorityService.getAuthority(physicalClusterId, topicName, appId))
        );
    }
}