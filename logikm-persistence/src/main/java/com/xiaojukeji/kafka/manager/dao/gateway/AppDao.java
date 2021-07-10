package com.xiaojukeji.kafka.manager.dao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/21
 */
public interface AppDao {
    /**
     * 创建appId
     * @param appDO appDO
     * @return int
     */
    int insert(AppDO appDO);

    /**
     * 删除appId
     * @param appName App名称
     * @return int
     */
    int deleteByName(String appName);

    /**
     * 获取principal名下的AppID
     * @param principal 负责人
     * @return List<AppDO>
     */
    List<AppDO> getByPrincipal(String principal);

    /**
     * 获取appId
     *
     * @param name app名称
     * @return AppDO
     */
    AppDO getByName(String name);

    /**
     * 获取appId
     *
     * @param appId appId信息
     * @return AppDO
     */
    AppDO getByAppId(String appId);

    /**
     * 获取所有的应用
     * @return List<AppDO>
     */
    List<AppDO> listAll();

    /**
     * 更新appId
     * @param appDO AppIdDO
     * @return int
     */
    int updateById(AppDO appDO);
}