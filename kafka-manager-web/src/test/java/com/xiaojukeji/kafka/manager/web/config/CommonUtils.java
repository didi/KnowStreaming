package com.xiaojukeji.kafka.manager.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xuguang
 * @Date 2022/1/11
 */
public class CommonUtils {

    private static final Logger      LOGGER           = LoggerFactory.getLogger(CommonUtils.class.getName());

    private static String            settingsFile     = "integrationTest-settings.properties";

    public static HttpHeaders getHttpHeaders() {
        // 需要在管控平台上配置，教程见docs -> user_guide -> call_api_bypass_login
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(ConfigConstant.TRICK_LOGIN_SWITCH, ConfigConstant.OPEN_TRICK_LOGIN);
        httpHeaders.add(ConfigConstant.TRICK_LOGIN_USER, ConfigConstant.ADMIN_USER);
        return httpHeaders;
    }

    /**
     * 读取本地配置
     *
     * @return
     * @throws IOException
     */
    public static Map<String, String> readSettings() throws IOException {
        InputStream is = CommonUtils.class.getClassLoader().getResourceAsStream(settingsFile);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            LOGGER.error("CommonUtil error!", "load " + settingsFile + " error, {}", e.getMessage());
        } finally {
            is.close();
        }
        return new HashMap<String, String>((Map) prop);
    }
}
