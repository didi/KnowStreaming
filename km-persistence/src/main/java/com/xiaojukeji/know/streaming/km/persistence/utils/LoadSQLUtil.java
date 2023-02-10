package com.xiaojukeji.know.streaming.km.persistence.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LoadSQLUtil {
    private static final ILog LOGGER = LogFactory.getLog(LoadSQLUtil.class);

    public static final String SQL_DDL_KS_KM            = "sql/ddl-ks-km.sql";
    public static final String SQL_DDL_LOGI_JOB         = "sql/ddl-logi-job.sql";
    public static final String SQL_DDL_LOGI_SECURITY    = "sql/ddl-logi-security.sql";
    public static final String SQL_DML_KS_KM            = "sql/dml-ks-km.sql";
    public static final String SQL_DML_LOGI             = "sql/dml-logi.sql";

    public static String loadSQL(String sqlFileName) {
        InputStream inputStream = LoadSQLUtil.class.getClassLoader().getResourceAsStream(sqlFileName);
        if (inputStream == null) {
            LOGGER.error("method=loadSQL||fileName={}||msg=read script failed", sqlFileName);
            return "";
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            LOGGER.error("method=loadSQL||fileName={}||errMsg={}||msg=read script failed", sqlFileName, e.getMessage());
        } finally {
            try {
                inputStream.close();


            } catch (IOException e) {
                LOGGER.error("method=loadSQL||fileName={}||errMsg={}||msg=close reading script failed", sqlFileName, e.getMessage());
            }
        }

        return "";
    }

    private LoadSQLUtil() {
    }
}
