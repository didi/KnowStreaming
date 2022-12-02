package com.xiaojukeji.know.streaming.km.common.component;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.exception.NotFindSubclassException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import java.util.Map;

/**
 * 从SpringContent中获取指定接口的类的实例
 * 使用例子参考 HandleFactory
 *
 * @author linyunan
 * @date 2021-04-25
 */
public abstract class BaseExtendFactory {

    private static final ILog LOGGER = LogFactory.getLog(BaseExtendFactory.class);

    public  <T> T getByClassNamePer(String classNamePre, Class<T> clazz) {
        T handler = null;
        try {
            handler = doGet(classNamePre, clazz);
        } catch (NotFindSubclassException e) {
            LOGGER.error("method=getByClassNamePer||handleNamePre={}||msg={}", classNamePre,
                e.getMessage());
        }

        return handler;
    }

    private <T> T doGet(String classNamePre, Class<T> clazz) {
        Map<String, T> beans = null;
        try {
            beans = SpringTool.getBeansOfType(clazz);
        } catch (BeansException e) {
            LOGGER.error("method=findFromSpringContext||handleNamePre={}||msg={}",
                classNamePre, e.getMessage());
        }

        if (beans == null || beans.isEmpty()) {
            throw new NotFindSubclassException( String.format("找不到【%s】的具体处理器【%s】", clazz.getSimpleName(), classNamePre));
        }

        for (Map.Entry<String, T> bean : beans.entrySet()) {
            if (StringUtils.startsWith(bean.getKey(), classNamePre)) {
                return bean.getValue();
            }
        }

        throw new NotFindSubclassException( String.format("找不到【%s】的具体处理器【%s】", clazz.getSimpleName(), classNamePre));
    }
}
