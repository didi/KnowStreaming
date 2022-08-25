/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.persistence.mysql.job;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaojukeji.know.streaming.km.common.bean.po.job.JobPO;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDAO extends BaseMapper<JobPO> {
    int addAndSetId(JobPO jobPO);
}
