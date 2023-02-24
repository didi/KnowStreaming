/**
 * @Author yunan
 * @Date 2017/9/27 18:24
 */
package com.xiaojukeji.know.streaming.km.common.component;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.log.common.Constants;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.exception.ThirdPartRemoteException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinbinbin
 * @version $Id: RestTemplateConfig.java, v 0.1 2018年09月27日 17:45 jinbinbin Exp $
 */
@Configuration
public class RestTemplateConfig {

    private static final ILog REQ_LOGGER    = LogFactory.getLog("thirdReqLogger");
    private static final ILog RESP_LOGGER   = LogFactory.getLog("thirdRespLogger");
    private static final ILog SYSTEM_LOGGER = LogFactory.getLog(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        // 2分钟超时
        simpleClientHttpRequestFactory.setConnectTimeout(2 * 60 * 1000);
        simpleClientHttpRequestFactory.setReadTimeout(60 * 1000);

        RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory));

        // 增加自定义的拦截器
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LogHttpRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        // 设置字符编码
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter stringConverter = (StringHttpMessageConverter) converter;
                stringConverter.setWriteAcceptCharset(false);
                stringConverter.setDefaultCharset( StandardCharsets.UTF_8);
            }
        }

        return restTemplate;
    }

    private class LogHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
            ClientHttpResponse response = null;
            String subFlag = LogFactory.getUniqueFlag();
            Long beginNano = System.nanoTime();
            try {

                //打印请求出口日志
                traceRequest(request, subFlag, body);

                request.getHeaders().add(Constants.FLAG, LogFactory.getFlag());
                request.getHeaders().add("traceid", LogFactory.getFlag());
                request.getHeaders().add("spanid", subFlag);

                response = execution.execute(request, body);

                // 打印请求响应日志
                traceResponse(request, response, null, subFlag, beginNano);

                return response;
            } catch (IOException exe) {
                try {
                    traceResponse(request, response, exe, subFlag, beginNano);
                } catch (Exception e) {
                    SYSTEM_LOGGER.warn("method=intercept||msg={}", e.getMessage());
                }
                throw new ThirdPartRemoteException("rest-template: " + exe.getMessage(), exe,
                        ResultStatus.HTTP_REQ_ERROR);
            }
        }

        private void traceRequest(HttpRequest request, String subFlag, byte[] body) throws IOException {
            REQ_LOGGER.info("method=traceRequest||remoteRequest||url={}||method={}||headers={}||body={}||subFlag={}",
                    request.getURI(), request.getMethod(), JSON.toJSONString(request.getHeaders()), new String(body, "UTF-8"), subFlag);
        }

        private void traceResponse(HttpRequest request, ClientHttpResponse response, IOException exception,
                                   String subFlag, long nanoTime) throws IOException {
            String url = simpleUrl(request);
            StringBuilder inputStringBuilder = new StringBuilder();
            if (response == null) {
                RESP_LOGGER.warn(
                    "method=traceResponse||remoteResponse||code=-1||url={}||text={}||headers={}||body={}||timeCost={}||subFlag={}",
                        url, null, null, null, (System.nanoTime() - nanoTime) / 1000 / 1000, subFlag);
                return;
            }

            try {
                try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getBody(), "UTF-8"))) {

                    boolean first = true;
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        if (!first) {
                            inputStringBuilder.append('\n');
                        } else {
                            first = false;
                        }
                        inputStringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }
                }
            } catch (Exception e) {
                RESP_LOGGER.warn(
                    "method=traceResponse||remoteResponse||code={}||url={}||text={}||headers={}||body={}||error={}||timeCost={}||subFlag={}",
                    response.getStatusCode(),
                        url,
                        response.getStatusText(),
                        response.getHeaders(),
                        inputStringBuilder.toString(),
                        e,
                        (System.nanoTime() - nanoTime) / 1000 / 1000,
                        subFlag
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new ThirdPartRemoteException(getResponseBodyAndIgnoreException(response), e, ResultStatus.HTTP_REQ_ERROR);
                }
            }

            String responseString = inputStringBuilder.toString().replace("\n", "");
            responseString = responseString.substring(0, Math.min(responseString.length(), 5000));

            if (!response.getStatusCode().is2xxSuccessful()) {
                if (exception == null) {
                    RESP_LOGGER.warn(
                        "method=traceResponse||remoteResponse||code={}||url={}||text={}||headers={}||body={}||timeCost={}||subFlag={}",
                        response.getStatusCode(), url, response.getStatusText(), response.getHeaders(), responseString,
                        (System.nanoTime() - nanoTime) / 1000 / 1000, subFlag);
                } else {
                    RESP_LOGGER.warn(
                        "method=traceResponse||remoteResponse||code={}||url={}||text={}||headers={}||body={}||error={}||timeCost={}||subFlag={}",
                        response.getStatusCode(), url, response.getStatusText(), response.getHeaders(), responseString,
                        exception, (System.nanoTime() - nanoTime) / 1000 / 1000, subFlag);
                }
                throw new ThirdPartRemoteException(responseString, ResultStatus.HTTP_REQ_ERROR);
            }

            if (exception == null) {
                RESP_LOGGER.info(
                    "method=traceResponse||remoteResponse||code={}||url={}||text={}||headers={}||responseBody={}||timeCost={}||subFlag={}",
                    response.getStatusCode(), url, response.getStatusText(), response.getHeaders(), responseString,
                    (System.nanoTime() - nanoTime) / 1000 / 1000, subFlag);
            } else {
                RESP_LOGGER.warn(
                    "method=traceResponse||remoteResponse||code={}||url={}||text={}||headers={}||responseBody={}||error={}||timeCost={}||subFlag={}",
                    response.getStatusCode(), url, response.getStatusText(), response.getHeaders(), responseString,
                    exception, (System.nanoTime() - nanoTime) / 1000 / 1000, subFlag);
            }

        }

    }

    private String getResponseBodyAndIgnoreException(ClientHttpResponse response) {
        try {
            byte[] bytes = new byte[response.getBody().available()];
            response.getBody().read(bytes);

            return new String(bytes);
        } catch (Exception e) {
            // ignore
        }

        return "";
    }

    private static String simpleUrl(HttpRequest request) {
        String url = request.getURI().toString();
        int index = url.indexOf("?");
        if (index > 0) {
            return url.substring(0, index);
        } else {
            return url;
        }
    }
}
