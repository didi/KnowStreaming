package com.xiaojukeji.know.streaming.km.persistence.mysql.zookeeper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.zookeeper.ZookeeperInfoPO;
import org.springframework.stereotype.Repository;

@Repository
public interface ZookeeperDAO extends BaseMapper<ZookeeperInfoPO> {
}
