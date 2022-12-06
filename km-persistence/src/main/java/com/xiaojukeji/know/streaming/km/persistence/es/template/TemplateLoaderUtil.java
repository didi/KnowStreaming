package com.xiaojukeji.know.streaming.km.persistence.es.template;

import com.google.common.collect.Maps;
import com.xiaojukeji.know.streaming.km.persistence.es.ESFileLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class TemplateLoaderUtil extends ESFileLoader {

    private static final String FILE_PATH = "es/template/";

    /**
     * 查询语句容器
     */
    private Map<String, String> templateMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        templateMap.putAll(loaderFileContext(FILE_PATH, TemplateConstant.class.getDeclaredFields()));
    }

    public String getContextByFileName(String fileName) {
        return templateMap.get(fileName);
    }
}
