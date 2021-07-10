package com.xiaojukeji.kafka.manager.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;


/**
 * @author zengqiao
 * @date 20/5/24
 */
public class HttpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    // 连接超时时间, 单位: ms
    private static int CONNECT_TIME_OUT = 15000;

    // 读取超时时间, 单位: ms
    private static int READ_TIME_OUT = 3000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    private static final String CHARSET_UTF8 = "UTF-8";

    private static final String FILE_PARAM = "filecontent";

    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    public static String get(String url, Map<String, String> params) {
        return sendRequest(url, METHOD_GET, params, null, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) {
        return sendRequest(url, METHOD_GET, params, headers, null);
    }

    public static String postForString(String url, String content, Map<String, String> headers) {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_POST, null, headers, in);
    }

    public static String uploadFile(String url,
                                    MultipartFile multipartFile,
                                    Map<String, String> bodies,
                                    Map<String, String> headers) {
        HttpPost post = new HttpPost(url);
        String response = "";
        try {

            if (!ValidateUtils.isEmptyMap(headers)) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName(CHARSET_UTF8));

            //加上此行代码解决返回中文乱码问题
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // 文件流
            builder.addBinaryBody(
                    FILE_PARAM,
                    multipartFile.getInputStream(),
                    ContentType.MULTIPART_FORM_DATA,
                    multipartFile.getOriginalFilename()
            );

            if (!ValidateUtils.isNull(bodies)) {
                for (Map.Entry<String, String> e : bodies.entrySet()) {
                    builder.addTextBody(e.getKey(), e.getValue());
                }
            }
            HttpEntity postEntity = builder.build();
            post.setEntity(postEntity);
            HttpEntity entity = HTTP_CLIENT.execute(post).getEntity();
            response = EntityUtils.toString(entity, CHARSET_UTF8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            post.releaseConnection();
        }
        return response;
    }

    public static String putForString(String url, String content, Map<String, String> headers) {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_PUT, null, headers, in);
    }

    public static String deleteForString(String url, String content, Map<String, String> headers) {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_DELETE, null, headers, in);
    }

    /**
     * @param url        请求的链接, 只支持 http 和 https 链接
     * @param method     GET or POST
     * @param headers    请求头 (将覆盖默认请求), 可以为 null
     * @param bodyStream 请求内容, 流将自动关闭, 可以为 null
     * @return 返回响应内容的文本
     * @throws Exception http 响应 code 非 200, 或发生其他异常均抛出异常
     */
    private static String sendRequest(String url,
                                      String method,
                                      Map<String, String> params,
                                      Map<String, String> headers,
                                      InputStream bodyStream) {
        HttpURLConnection conn = null;
        try {
            String paramUrl = setUrlParams(url, params);

            // 打开链接
            URL urlObj = new URL(paramUrl);
            conn = (HttpURLConnection) urlObj.openConnection();

            // 设置conn属性
            setConnProperties(conn, method, headers);

            // 设置请求内容
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }

            return handleResponseBodyToString(conn.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(conn);
        }
    }

    private static String setUrlParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static void setConnProperties(HttpURLConnection conn,
                                          String method,
                                          Map<String, String> headers) throws Exception {
        // 设置连接超时时间
        conn.setConnectTimeout(CONNECT_TIME_OUT);

        // 设置读取超时时间
        conn.setReadTimeout(READ_TIME_OUT);

        // 设置请求方法
        if (method != null && !method.isEmpty()) {
            conn.setRequestMethod(method);
        }

        // 添加请求头
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        ByteArrayOutputStream bytesOut = null;
        try {
            bytesOut = new ByteArrayOutputStream();
            copyStreamAndClose(in, bytesOut);
            return new String(bytesOut.toByteArray(), CHARSET_UTF8);
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
                LOGGER.error("close connection failed", e);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                LOGGER.error("close stream failed", e);
            }
        }
    }
}
