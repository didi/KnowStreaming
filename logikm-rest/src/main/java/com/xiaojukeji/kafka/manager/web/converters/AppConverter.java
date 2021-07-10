package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicAuthorityEnum;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.QuotaVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppTopicAuthorityVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
public class AppConverter {
    public static List<AppVO> convert2AppVOList(List<AppDO> doList) {
        if (ValidateUtils.isNull(doList)) {
            return new ArrayList<>();
        }

        List<AppVO> voList = new ArrayList<>();
        for (AppDO elem: doList) {
            voList.add(convert2AppVO(elem));
        }
        return voList;
    }

    public static AppVO convert2AppVO(AppDO appDO) {
        if (ValidateUtils.isNull(appDO)) {
            return null;
        }

        AppVO vo = new AppVO();
        vo.setAppId(appDO.getAppId());
        vo.setName(appDO.getName());
        vo.setPassword(appDO.getPassword());
        vo.setDescription(appDO.getDescription());
        vo.setPrincipals(appDO.getPrincipals());
        return vo;
    }

    public static List<QuotaVO> convert2QuotaVOList(Long clusterId, TopicQuota quotaDO) {
        if (ValidateUtils.isNull(quotaDO)) {
            return null;
        }

        QuotaVO vo = new QuotaVO();
        vo.setClusterId(clusterId);
        vo.setTopicName(quotaDO.getTopicName());
        vo.setAppId(quotaDO.getAppId());
        vo.setProduceQuota(quotaDO.getProduceQuota());
        vo.setConsumeQuota(quotaDO.getConsumeQuota());
        return Arrays.asList(vo);
    }

    public static AppTopicAuthorityVO convert2AppTopicAuthorityVO(String appId,
                                                                  String topicName,
                                                                  AuthorityDO authority) {
        AppTopicAuthorityVO vo = new AppTopicAuthorityVO();
        vo.setAppId(appId);
        vo.setTopicName(topicName);
        vo.setAccess(ValidateUtils.isNull(authority) ? TopicAuthorityEnum.DENY.getCode() : authority.getAccess());
        return vo;
    }
}