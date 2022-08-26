package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class GitPropUtil {
    private static final ILog log = LogFactory.getLog(GitPropUtil.class);

    private static Properties props = null;

    public static final String VERSION_FIELD_NAME = "git.build.version";

    public static final String COMMIT_ID_FIELD_NAME = "git.commit.id.abbrev";

    public static String getProps(String fieldName) {
        if (props == null) {
            props = ConvertUtil.toObj(readGitPropertiesInJarFile(), Properties.class);
        }

        return props.getProperty(fieldName);
    }

    public static Properties getProps() {
        if (props == null) {
            props = ConvertUtil.toObj(readGitPropertiesInJarFile(), Properties.class);
        }

        return props;
    }

    private static String readGitPropertiesInJarFile() {
        InputStream inputStream = null;
        try {
            inputStream = GitPropUtil.class.getClassLoader().getResourceAsStream("git.properties");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("method=readGitPropertiesInJarFile||errMsg=exception.");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.warn("method=readGitPropertiesInJarFile||msg=close failed||errMsg=exception.");
            }
        }

        return "{}";
    }

    private GitPropUtil() {
    }
}
