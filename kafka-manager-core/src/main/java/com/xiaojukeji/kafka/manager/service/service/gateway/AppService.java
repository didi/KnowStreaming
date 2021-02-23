package com.xiaojukeji.kafka.manager.service.service.gateway;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.AppTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.AppDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/4/28
 */
public interface AppService {
    /**
     * 插入数据
     * @param appDO appDO
     * @return int
     */
    ResultStatus addApp(AppDO appDO, String operator);

    /**
     * 删除数据
     * @param appDO appDO
     * @return int
     */
    int deleteApp(AppDO appDO, String operator);

    /**
     * 通过负责人来查找
     * @param name app名称
     * @return List<AppDO>
     */
    AppDO getByName(String name);

    /**
     * 更新App信息
     * @param dto app信息
     * @param operator 操作人
     * @param adminApi admin操作
     * @author zengqiao
     * @date 20/5/4
     * @return int
     */
    ResultStatus updateByAppId(AppDTO dto, String operator, Boolean adminApi);

    /**
     * 通过负责人来查找
     * @param principal 负责人
     * @return List<AppDO>
     */
    List<AppDO> getByPrincipal(String principal);

    /**
     * 通过appId来查,需要check当前登录人是否有权限.
     * @param appId appId
     * @return AppDO
     */
    AppDO getAppByUserAndId(String appId, String curUser);

    /**
     * 通过appId来查
     * @param appId appId
     * @return AppDO
     */
    AppDO getByAppId(String appId);

    /**
     * 查找所有
     * @return List<TopicDO>
     */
    List<AppDO> listAll();

    List<AppTopicDTO> getAppTopicDTOList(String appId, Boolean mine);

    boolean verifyAppIdByPassword(String appId, String password);
}
