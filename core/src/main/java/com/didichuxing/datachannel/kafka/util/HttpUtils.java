/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

/**
 * HTTP 请求工具
 *
 *     // GET 请求, 超时时间设置为0, 表示只请求一次, 返回响应文本
 *     String result = HttpUtils.get("http://www.baidu.com", 0);
 *
 *     // GET 请求, 5s超时, 返回响应文本
 *     Map<String, String> paramMap = new HashMap<>();
 *     paramMap.put("key", "value");
 *     HttpUtils.get("http://www.baidu.com", paramMap, 5000);
 *
 *     // POST 请求, 10s超时时间, 返回响应文本
 *     String respText = HttpUtils.post("http://url", null, "data".getBytes(), 10000);
 *
 *     // 还有其他若干 get(...) 和 post(...) 方法的重载(例如请求时单独添加请求头), 详见代码实现
 *
 */

public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    // 返回结果最大长度, 10MB
    private static final int RESPONSE_DATA_MAX_LENGTH = 10 * 1024 * 1024;

    // 连接超时时间, 单位: ms
    private static int CONNECT_TIME_OUT = 5 * 1000;

    // 读取超时时间, 单位: ms
    private static int READ_TIME_OUT = 30 * 1000;

    private static boolean fastFailed = true;

    // 设置编码格式
    private static String CONTENT_TYPE = "application/json;charset=UTF-8";

    public static ResponseCommonResult get(String url, Map<String, String> params, int timeoutMs) {
        return get(url, params, null, timeoutMs);
    }

    public static ResponseCommonResult get(String url, Map<String, String> params, Map<String, String> headers, int timeoutMs) {
        return sendRequest(url, params,"GET", headers, null, timeoutMs);
    }

    public static ResponseCommonResult post(String url, Map<String, String> params, byte[] body, int timeoutMs) {
        return post(url, params, null, body, timeoutMs);
    }

    public static ResponseCommonResult post(String url, Map<String, String> params, Map<String, String> headers, byte[] body, int timeoutMs) {
        InputStream in = null;
        if (body != null && body.length > 0) {
            in = new ByteArrayInputStream(body);
        }
        return post(url, params, headers, in, timeoutMs);
    }

    public static ResponseCommonResult post(String url, Map<String, String> params, InputStream bodyStream, int timeoutMs) {
        return post(url, params,null, bodyStream, timeoutMs);
    }

    public static ResponseCommonResult post(String url, Map<String, String> params, Map<String, String> headers, InputStream bodyStream, int timeoutMs) {
        return sendRequest(url, params,"POST", headers, bodyStream, timeoutMs);
    }

    private static ResponseCommonResult sendRequest(String url, Map<String, String> params, String method, Map<String, String> headers, InputStream bodyStream, int timeoutMs) {
        log.debug(String.format("sendRequest params detail. url:%s, params:%s, method:%s, headers:%s, bodyStream:%s, timeoutMs:%d", url, params == null ? "null" : params.toString(), method, headers == null ? "null" : headers.toString(), bodyStream == null ? "null" : bodyStream.toString(), timeoutMs));
        try {
            if (timeoutMs < 0) {
                return ResponseCommonResult.failure("timeoutMs must be positive or 0");
            }

            ResponseCommonResult result = sendRequestInternal(url, params, method, headers, bodyStream);

            if (timeoutMs == 0 || fastFailed) {
                return result;
            }

            long timeoutTimestamp = System.currentTimeMillis() + timeoutMs;
            // random sleep [1s,5s]
            int backOffMs = (new Random().nextInt(5) + 1) * 1000;
            while (result.getCode() != ResponseCommonResult.SUCCESS_STATUS) {
                if (System.currentTimeMillis() > timeoutTimestamp) {
                    return result;
                }

                String paramaters = params == null ? "null" : params.toString();
                String body = bodyStream == null ? "null" : bodyStream.toString();
                log.warn("send request failed, request url:{}, request parameters:{}, body:{}, backOff milliseconds:{}", url, paramaters, body, backOffMs);
                try {
                    Thread.sleep(backOffMs);
                } catch (InterruptedException e) {
                    log.error("Interrupted when do url request, detail: ", e);
                }

                result = sendRequestInternal(url, params, method, headers, bodyStream);
                backOffMs = (new Random().nextInt(5) + 1) * 1000;
            }

            return result;
        } catch (Exception e) {
            return ResponseCommonResult.failure(e.getMessage());
        }
    }

    /**
     * @param url        请求的链接, 只支持 http 和 https 链接
     * @param method     GET or POST
     * @param headers    请求头 (将覆盖默认请求), 可以为 null
     * @param bodyStream 请求内容, 流将自动关闭, 可以为 null
     * @return 返回响应内容的文本
     * @throws Exception http 响应 code 非 200, 或发生其他异常均抛出异常
     */
    private static ResponseCommonResult sendRequestInternal(String url, Map<String, String> params, String method, Map<String, String> headers, InputStream bodyStream) throws Exception {
        assertUrlValid(url);

        HttpURLConnection conn = null;

        try {
            String paramUrl = url;
            if (params != null) {
                paramUrl = setUrlParams(url, params);
            }

            // 打开链接
            URL urlObj = new URL(paramUrl);
            conn = (HttpURLConnection) urlObj.openConnection();

            // 设置各种默认属性
            setDefaultProperties(conn);

            // 设置请求方法
            if (method != null && method.length() > 0) {
                conn.setRequestMethod(method);
            }

            // 添加请求头
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 设置请求内容
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }

            // 获取响应code
            int code = conn.getResponseCode();

            // 获取响应内容长度
            long contentLength = conn.getContentLengthLong();

            // 获取响应内容输入流
            InputStream in = conn.getInputStream();

            if (contentLength > RESPONSE_DATA_MAX_LENGTH) {
                throw new IOException(String.format("Response content length too large: %d", contentLength));
            }

            String resultStr = handleResponseBodyToString(in);

            // 没有响应成功, 均抛出异常
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException(String.format("Http Error: %d, detail: %s", code, resultStr));
            }

            return ResponseCommonResult.success(resultStr);

        } catch (Exception e) {
            return ResponseCommonResult.failure(e.getMessage());
        } finally {
            closeConnection(conn);
        }
    }

    private static void assertUrlValid(String url) throws IllegalAccessException {
        if (url == null || !(url.startsWith("http://") || url.startsWith("https://")))
            throw new IllegalAccessException("url cannot be null");

        url = url.toLowerCase();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            throw new IllegalAccessException(String.format("Only support http or https url:%s", url));
    }

    private static String setUrlParams(String url, Map<String, String> params) {
        String paramUrl = url + "?";
        for (Map.Entry<String, String> entry: params.entrySet()) {
            String kv = entry.getKey() + "=" + entry.getValue() + "&";
            paramUrl += kv;
        }
        return paramUrl.substring(0, paramUrl.length()-1);
    }

    private static void setDefaultProperties(HttpURLConnection conn) {
        // 设置连接超时时间
        conn.setConnectTimeout(CONNECT_TIME_OUT);

        // 设置读取超时时间
        conn.setReadTimeout(READ_TIME_OUT);

        // 设置编码格式
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        ByteArrayOutputStream bytesOut = null;

        try {
            bytesOut = new ByteArrayOutputStream();

            // 读取响应内容
            copyStreamAndClose(in, bytesOut);

            // 响应内容的字节序列
            byte[] contentBytes = bytesOut.toByteArray();

            return new String(contentBytes, "utf-8");

        } finally {
            closeStream(bytesOut);
        }
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                log.error("close httpURLConnection fail", e);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                log.error("close stream fail", e);
            }
        }
    }

    public static void setFastFailed(boolean value) {
        fastFailed = value;
    }
}

