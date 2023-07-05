package com.xiaojukeji.know.streaming.km.persistence.es.dsls;

import com.didiglobal.logi.log.ILog;
import com.google.common.collect.Maps;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import com.xiaojukeji.know.streaming.km.persistence.es.ESFileLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author: D10865
 * @description:
 * @date: Create on 2019/2/28 上午10:08
 * @modified By D10865
 *
 * 加载dsl查询语句工具类
 *
 */
@Component
public class DslLoaderUtil extends ESFileLoader {
    private static final ILog LOGGER  = LoggerUtil.getESLogger();

    private static final String FILE_PATH = "es/dsl/";

    /**
     * 查询语句容器
     * key   : fileRelativePath
     * value : dslContent
     */
    private Map<String, String> dslsMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        dslsMap.putAll(loaderFileContext(FILE_PATH, DslConstant.class.getDeclaredFields()));
    }

    /**
     * 获取查询语句
     */
    public String getDslByFileName(String fileName) {
        return dslsMap.get(fileName);
    }

    /**
     * 获取格式化的查询语句
     */
    public String getFormatDslByFileName(String fileName, Object... args) {
        String loadDslContent = getDslByFileName(fileName);

        if (StringUtils.isBlank(loadDslContent)) {
            LOGGER.error("method=getFormatDslByFileName||errMsg=dsl file {} content is empty",
                fileName);
            return "";
        }

        // 格式化查询语句
        String dsl = trimJsonBank( String.format(loadDslContent, args));
        // 如果不是线上环境，则输出dsl语句
        if (!EnvUtil.isOnline()) {
            LOGGER.info("method=getFormatDslByFileName||dsl={}", dsl);
        }

        return dsl;
    }
}
