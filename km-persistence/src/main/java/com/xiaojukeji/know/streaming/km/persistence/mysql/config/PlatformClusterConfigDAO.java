package com.xiaojukeji.know.streaming.km.persistence.mysql.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.config.PlatformClusterConfigPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformClusterConfigDAO extends BaseMapper<PlatformClusterConfigPO> {
    int batchReplace(List<PlatformClusterConfigPO> poList);
}
