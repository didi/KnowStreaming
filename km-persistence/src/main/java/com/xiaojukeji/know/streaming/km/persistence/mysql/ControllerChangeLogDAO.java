package com.xiaojukeji.know.streaming.km.persistence.mysql;

import com.xiaojukeji.know.streaming.km.common.bean.po.ControllerChangeLogPO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ControllerChangeLogDAO {

    int insert(ControllerChangeLogPO controllerChangeLogPO);

    List<ControllerChangeLogPO> paginationQuery(Map<String, Object> params);

    Long queryCount(Map<String, Object> params);

}
