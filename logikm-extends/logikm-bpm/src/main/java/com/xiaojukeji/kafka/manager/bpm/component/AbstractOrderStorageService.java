package com.xiaojukeji.kafka.manager.bpm.component;

import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/11
 */
public abstract class AbstractOrderStorageService {
    public abstract ResultStatus directSaveHandledOrder(OrderDO orderDO);

    public abstract boolean save(OrderDO orderDO);

    public abstract ResultStatus cancel(Long id, String username);

    public abstract OrderDO getById(Long id);

    public abstract List<OrderDO> list();

    public abstract int updateOrderById(OrderDO orderDO);

    public abstract List<OrderDO> getByStatus(Integer status);

    public abstract List<OrderDO> getByApplicantAndStatus(String applicant, Integer status);

    public abstract List<OrderDO> getByApproverAndStatus(String approver, Integer status);

    public abstract List<OrderDO> getByGmtHandle(Date startTime);

    public abstract List<OrderDO> getByHandleTime(Date startTime, Date endTime);

    public abstract int updateExtensionsById(OrderDO orderDO);
}