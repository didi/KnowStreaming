package com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.connector;

import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.BaseConnectorMetricESDAO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_CONNECTOR_INDEX;

@Component
public class ConnectorMetricESDAO extends BaseConnectorMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName     = CONNECT_CONNECTOR_INDEX;
        checkCurrentDayIndexExist();
        register( this);
    }
}
