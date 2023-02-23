package com.xiaojukeji.know.streaming.km.license.service;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

public interface LicenseService {

    /**
     * 是否达到了 license 现在的集群数量
     * @return
     */
    Result<Void> addClusterLimit();

    /**
     * 校验 license 是否通过
     * @return
     */
    Result<Void> check();
}
