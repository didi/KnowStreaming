package com.xiaojukeji.know.streaming.km.persistence.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didiglobal.logi.log.ILog;
import com.google.common.collect.Lists;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author didi
 */
public class ESFileLoader {
    private static final ILog LOGGER  = LoggerUtil.getESLogger();

    public Map<String, String> loaderFileContext(String filePath, Field[] fields) {
        LOGGER.info("method=loaderFileContext||DslLoaderUtil init start.");
        List<String>        dslFileNames   = Lists.newLinkedList();
        Map<String, String> fileContextMap = new HashMap<>();

        if(null == fields || 0 == fields.length){
            return fileContextMap;
        }

        // 反射获取接口中定义的变量中的值
        for (int i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);
            try {
                dslFileNames.add(fields[i].get(null).toString());
            } catch (IllegalAccessException e) {
                LOGGER.error("method=loaderFileContext||errMsg=fail to read {} error. ", fields[i].getName(),
                        e);
            }
        }

        // 加载dsl文件及内容
        for (String fileName : dslFileNames) {
            fileContextMap.put(fileName, readEsFileInJarFile(filePath, fileName));
        }

        // 输出加载的查询语句
        LOGGER.info("method=loaderFileContext||msg=dsl files count {}", fileContextMap.size());
        for (Map.Entry<String/*fileRelativePath*/, String/*dslContent*/> entry : fileContextMap.entrySet()) {
            LOGGER.info("method=loaderFileContext||msg=file name {}, dsl content {}", entry.getKey(),
                    entry.getValue());
        }

        LOGGER.info("method=loaderFileContext||DslLoaderUtil init finished.");

        return fileContextMap;
    }

    /**
     * 去除json中的空格
     *
     * @param sourceDsl
     * @return
     */
    public String trimJsonBank(String sourceDsl) {
        List<String> dslList = Lists.newArrayList();

        DefaultJSONParser parser = null;
        Object obj = null;
        String dsl = sourceDsl;

        // 解析多个json，直到pos为0
        for (;;) {
            try {
                // 这里需要Feature.OrderedField.getMask()保持有序
                parser = new DefaultJSONParser(dsl, ParserConfig.getGlobalInstance(),
                        JSON.DEFAULT_PARSER_FEATURE | Feature.OrderedField.getMask());
                obj = parser.parse();
            } catch (Exception t) {
                LOGGER.error("method=trimJsonBank||errMsg=parse json {} error. ", dsl, t);
            }
            if (obj == null) {
                break;
            }

            if (obj instanceof JSONObject) {
                dslList.add( JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue));
                int pos = parser.getLexer().pos();
                if (pos <= 0) {
                    break;
                }
                dsl = dsl.substring(pos);
                parser.getLexer().close();
            } else {
                parser.getLexer().close();
                break;
            }
        }

        // 格式化异常或者有多个查询语句，返回原来的查询语句
        if (dslList.isEmpty() || dslList.size() > 1) {
            return sourceDsl;
        }

        return dslList.get(0);
    }

    /**
     * 从jar包中读取es相关的语句文件
     *
     * @param fileName
     * @return
     */
    private String readEsFileInJarFile(String filePath, String fileName) {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream( filePath + fileName);

        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            List<String> lines = Lists.newLinkedList();
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                return StringUtils.join(lines, "");

            } catch (IOException e) {
                LOGGER.error("method=readDslFileInJarFile||errMsg=read file {} error. ", fileName,
                        e);

                return "";
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error(
                            "method=readDslFileInJarFile||errMsg=fail to close file {} error. ",
                            fileName, e);
                }
            }
        } else {
            LOGGER.error("method=readDslFileInJarFile||errMsg=fail to read file {} content",
                    fileName);
            return "";
        }
    }
}
