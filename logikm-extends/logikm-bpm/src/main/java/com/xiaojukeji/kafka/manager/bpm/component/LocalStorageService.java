package com.xiaojukeji.kafka.manager.bpm.component;

import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.OrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/11
 */
@Service("orderStorageService")
public class LocalStorageService extends AbstractOrderStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageService.class);

    @Autowired
    private OrderDao orderDao;

    @Override
    public ResultStatus directSaveHandledOrder(OrderDO orderDO) {
        try {
            if (orderDao.directSaveHandledOrder(orderDO) <= 0) {
                return ResultStatus.MYSQL_ERROR;
            }
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("add order failed, orderDO:{}.", orderDO, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public boolean save(OrderDO orderDO) {
        try {
            if (orderDao.insert(orderDO) <= 0) {
                return false;
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("add order failed, orderDO:{}.", orderDO, e);
        }
        return false;
    }

    @Override
    public ResultStatus cancel(Long id, String username) {
        try {
            OrderDO orderDO = orderDao.getById(id);
            if (ValidateUtils.isNull(orderDO)) {
                return ResultStatus.ORDER_NOT_EXIST;
            }
            if (!username.equals(orderDO.getApplicant())) {
                return ResultStatus.USER_WITHOUT_AUTHORITY;
            }
            if (orderDao.updateOrderStatusById(id, OrderStatusEnum.CANCELLED.getCode()) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("cancel order failed, id:{}.", id, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public OrderDO getById(Long id) {
        try {
            return orderDao.getById(id);
        } catch (Exception e) {
            LOGGER.error("get order failed, id:{}.", id, e);
        }
        return null;
    }

    @Override
    public List<OrderDO> list() {
        try {
            return orderDao.list();
        } catch (Exception e) {
            LOGGER.error("get all order failed.", e);
        }
        return null;
    }

    @Override
    public int updateOrderById(OrderDO orderDO) {
        try {
            return orderDao.updateOrderById(orderDO);
        } catch (Exception e) {
            LOGGER.error("update order failed, orderDO:{}.", orderDO, e);
        }
        return 0;
    }

    @Override
    public List<OrderDO> getByStatus(Integer status) {
        try {
            return orderDao.getByStatus(status);
        } catch (Exception e) {
            LOGGER.error("get by status failed, status:{}.", status, e);
        }
        return null;
    }

    @Override
    public List<OrderDO> getByApplicantAndStatus(String applicant, Integer status) {
        return orderDao.getByApplicantAndStatus(applicant, status);
    }

    @Override
    public List<OrderDO> getByApproverAndStatus(String approver, Integer status) {
        return orderDao.getByApproverAndStatus(approver, status);
    }

    @Override
    public List<OrderDO> getByGmtHandle(Date startTime) {
        return orderDao.getByGmtHandle(startTime);
    }

    @Override
    public List<OrderDO> getByHandleTime(Date startTime, Date endTime) {
        return orderDao.getByHandleTime(startTime, endTime);
    }

    @Override
    public int updateExtensionsById(OrderDO orderDO) {
        return orderDao.updateExtensionsById(orderDO);
    }
}