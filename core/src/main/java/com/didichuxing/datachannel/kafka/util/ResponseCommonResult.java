/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.util;


import com.alibaba.fastjson.JSONObject;

public class ResponseCommonResult<T> {

    public static final int SUCCESS_STATUS  = 0;

    public static final int FAILED_STATUS   = -1;

    public static final String SUCCESS_MESSAGE = "process succeeded!";

    public static final String FAILED_MESSAGE  = "process failed!";

    private int              code;

    private String             message;

    private T                  data;

    public static <T> ResponseCommonResult<T> success(T data) {
        ResponseCommonResult<T> responseCommonResult = new ResponseCommonResult<T>();
        responseCommonResult.setMessage(SUCCESS_MESSAGE);
        responseCommonResult.setCode(SUCCESS_STATUS);
        responseCommonResult.setData(data);
        return responseCommonResult;
    }

    public static <T> ResponseCommonResult<T> success() {
        ResponseCommonResult<T> responseCommonResult = new ResponseCommonResult<T>();
        responseCommonResult.setCode(SUCCESS_STATUS);
        responseCommonResult.setMessage(SUCCESS_MESSAGE);
        return responseCommonResult;
    }

    public static <T> ResponseCommonResult<T> failure() {
        ResponseCommonResult<T> responseCommonResult = new ResponseCommonResult<T>();
        responseCommonResult.setMessage(FAILED_MESSAGE);
        responseCommonResult.setCode(FAILED_STATUS);
        return responseCommonResult;
    }

    public static <T> ResponseCommonResult<T> failure(String message) {
        ResponseCommonResult<T> responseCommonResult = new ResponseCommonResult<T>();
        responseCommonResult.setMessage(message);
        responseCommonResult.setCode(FAILED_STATUS);
        return responseCommonResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        jsonObject.put("data", data);
        return jsonObject.toJSONString();
    }
}
