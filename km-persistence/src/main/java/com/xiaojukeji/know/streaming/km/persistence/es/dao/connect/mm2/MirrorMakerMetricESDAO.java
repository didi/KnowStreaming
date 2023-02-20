package com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.mm2;

import com.xiaojukeji.know.streaming.km.persistence.es.dao.connect.BaseConnectorMetricESDAO;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_MM2_INDEX;

@Component
public class MirrorMakerMetricESDAO extends BaseConnectorMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName     = CONNECT_MM2_INDEX;
        checkCurrentDayIndexExist();
        register( this);
    }
}
