package com.xiaojukeji.kafka.manager.account.component;

import com.xiaojukeji.kafka.manager.account.common.EnterpriseStaff;

import java.util.List;

/**
 * 企业员工组织信息
 * @author zengqiao
 * @date 20/8/20
 */
public abstract class AbstractEnterpriseStaffService {
    public abstract EnterpriseStaff getEnterpriseStaffByName(String username);

    public abstract List<EnterpriseStaff> searchEnterpriseStaffByKeyWord(String keyWord);
}