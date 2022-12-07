
package com.xiaojukeji.know.streaming.km.common.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jinbinbin
 * @version $Id: RestHelper.java, v 0.1 2018年09月19日 17:31 jinbinbin Exp $
 */
@Component
public class RestTool {
    private static final ILog LOGGER  = LogFactory.getLog(RestTool.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * POST请求
     * @param url 请求地址
     * @param postBody 请求内容
     * @param headers header
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return
     */
    public <T> T postObjectWithRawContent(String url, Object postBody, HttpHeaders headers, Class<T> resultType) {
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(postBody, headers),
                String.class
        );

        return ConvertUtil.toObj(result.getBody(), resultType);
    }

    /**
     * POST请求
     * @param url 请求地址
     * @param postBody 请求内容
     * @param resultType 期望返回的类型
     * @param <T> 泛型T
     * @return T 
     */
    public <T> T postObjectWithJsonContent(String url, Object postBody, Class<T> resultType) {
        return this.postObjectWithRawContent(url, postBody, this.getJsonContentHeaders(), resultType);
    }

    /**
     * POST请求
     * @param url 请求地址
     * @param postBody 请求内容
     * @param resultType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T postObjectWithJsonContentAndHeader(String url, Map<String, String> headers, Object postBody, Class<T> resultType) {
        return this.postObjectWithRawContent(url, postBody, this.getJsonContentHeaders(headers), resultType);
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param params 请求参数
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getObjectWithJsonContent(String url, Map<String, ?> params, Class<T> resultType) {
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class,
                params
        );

        return ConvertUtil.toObj(result.getBody(), resultType);
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getForObject(String url, HttpHeaders headers, Type resultType) {
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class
        );

        return ConvertUtil.toObj(result.getBody(), resultType);
    }


    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getObjectWithParamsAndHeader(String url, Map<String, String> headers,
                                              Map<String, ?> params, Type resultType) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,
            new HttpEntity<>(null, getJsonContentHeaders(headers)), String.class, params);
        return ConvertUtil.toObj(result.getBody(), resultType);
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getForObject(String url, Map<String, String> headers, Type resultType) {
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(null, getJsonContentHeaders(headers)), String.class);
            return JSON.parseObject(result.getBody(), resultType);
        } catch (Exception e){
            LOGGER.error("method=getForObject||url={}||msg=exception!", url, e);
        }

        return null;
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getForObject(String url, HttpHeaders headers, TypeReference<T> resultType) {
        try {
            ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(null, headers), String.class);
            return JSON.parseObject(result.getBody(), resultType);
        } catch (Exception e){
            LOGGER.error("method=getForObject||url={}||msg=exception!", url, e);
        }

        return null;
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @return T
     */
    public String getForString(String url, Map<String, String> headers) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET,
            new HttpEntity<>(null, getJsonContentHeaders(headers)), String.class);
        return result.getBody();
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param params 请求参数
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> List<T> getArrayObjectWithJsonContent(String url, Map<String, ?> params, Class<T> resultType) {
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class,
                params
        );

        return ConvertUtil.str2ObjArrayByJson(result.getBody(), resultType);
    }

    /**
     * 根据map中的参数构建url+queryString
     * @param url 请求地址
     * @param params 请求参数
     * @return  请求url
     */
    public static String getQueryString(String url, Map<String, ?> params) {
        if (params == null) {
            return url;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        return builder.toUriString();
    }

    /**
     * DEL请求
     * @param url 请求地址
     */
    public void deleteObject(String url) {
        restTemplate.delete(url);
    }


    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T deleteWithParamsAndHeader(String url, Map<String, String> headers,
                                           Map<String, ?> params, Type resultType) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.DELETE,
            new HttpEntity<>(params, getJsonContentHeaders(headers)), String.class);
        return ConvertUtil.toObj(result.getBody(), resultType);
    }

    /**
     * GET请求  header中有application/json
     * @param url 请求地址
     * @param resultType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T getObjectWithJsonContent(String url, Type resultType) {
        HttpHeaders headers = getJsonContentHeaders();
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers),
            String.class);
        return ConvertUtil.toObj(result.getBody(), resultType);
    }

    /**
     * 构建 application/json header
     * @return HttpHeaders
     */
    private HttpHeaders getJsonContentHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    /**
     * 构建 application/json header
     * @return HttpHeaders
     */
    private HttpHeaders getJsonContentHeaders(Map<String, String> headMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.add( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }
        return headers;
    }

    /**
     * PUT请求 header中有application/json
     * @param url 请求地址
     * @param request 请求内容
     * @param responseType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T putJsonForObject(String url, Object request, Class<T> responseType) {
        HttpHeaders jsonHead = getJsonContentHeaders();
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,
            new HttpEntity<Object>(ConvertUtil.obj2Json(request), jsonHead), responseType);
        return responseEntity.getBody();
    }

    /**
     * PUT请求
     * @param url 请求地址
     * @param request 请求内容
     * @param responseType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T putForObject(String url, Object request, Type responseType) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT,
            new HttpEntity<>(ConvertUtil.obj2Json(request)), String.class);
        return ConvertUtil.toObj(result.getBody(), responseType);
    }

    /**
     * PUT请求
     * @param url 请求地址
     * @param request 请求内容
     * @param responseType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T putForObject(String url, HttpHeaders headers, String request, Type responseType) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT,
                new HttpEntity<>(request, headers), String.class);
        return ConvertUtil.toObj(result.getBody(), responseType);
    }

    /**
     * POST请求
     * @param url 请求地址
     * @param request 请求内容
     * @param responseType 期望返回的类型
     * @param <T> 泛型T
     * @return T
     */
    public <T> T putObjectWithJsonContentAndHeader(String url, Map<String, String> headers, Object request,
                                                   Type responseType) {
        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.PUT,
            new HttpEntity<Object>(ConvertUtil.obj2Json(request), getJsonContentHeaders(headers)), String.class);
        return ConvertUtil.toObj(result.getBody(), responseType);
    }

    /**
     * GET请求
     * @param url 请求地址
     * @param headers 请求头
     * @param resultType 返回类型
     * @param <T> 泛型T
     * @return T
     * */
    public <T> T deleteForObject(String url, Map<String, String> headers, Type resultType) {
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(null, getJsonContentHeaders(headers)),
                String.class
        );

        return ConvertUtil.toObj(result.getBody(), resultType);
    }

}
