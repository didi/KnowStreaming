package com.xiaojukeji.kafka.manager.bpm.order;

import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.BaseOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
public abstract class AbstractOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOrder.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    /**
     * 检查扩展字段并生成工单的Title
     * @param extensions 扩展字段
     * @return Result<String>
     */
    public abstract Result<String> checkExtensionFieldsAndGenerateTitle(String extensions);

    /**
     * 获取工单详情
     * @param orderDO 工单
     * @return BaseOrderDetailData
     */
    public BaseOrderDetailData getOrderDetail(OrderDO orderDO) {
        if (ValidateUtils.isNull(orderDO)) {
            return null;
        }
        BaseOrderDetailData baseDetail = new BaseOrderDetailData();
        baseDetail.setDescription(orderDO.getDescription());
        baseDetail.setGmtCreate(orderDO.getGmtCreate());
        baseDetail.setGmtHandle(orderDO.getGmtHandle());
        baseDetail.setId(orderDO.getId());
        baseDetail.setOpinion(orderDO.getOpinion());
        baseDetail.setStatus(orderDO.getStatus());
        baseDetail.setType(orderDO.getType());
        baseDetail.setTitle(orderDO.getTitle());
        try {
            // 获取具体工单的详情
            baseDetail.setDetail(this.getOrderExtensionDetailData(orderDO.getExtensions()));
        } catch (Exception e) {
            LOGGER.error("get order detail failed, req:{}", orderDO.getExtensions(), e);
        }
        baseDetail.setApplicant(accountService.getAccountFromCache(orderDO.getApplicant()));
        baseDetail.setApproverList(getApproverList(orderDO));
        return baseDetail;
    }

    protected abstract AbstractOrderDetailData getOrderExtensionDetailData(String extensions);

    private List<Account> getApproverList(OrderDO orderDO) {
        if (ValidateUtils.isNull(orderDO)) {
            return new ArrayList<>();
        }
        if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(orderDO.getStatus())) {
            // 工单已处理, 获取处理人的信息
            return Arrays.asList(accountService.getAccountFromCache(orderDO.getApprover()));
        }
        try {
            List<Account> approverList = getApproverList(orderDO.getExtensions());
            if (!ValidateUtils.isEmptyList(approverList)) {
                return approverList;
            }
        } catch (Exception e) {
            LOGGER.error("get approver list failed, req:{}", orderDO.getExtensions(), e);
        }
        return accountService.getAdminOrderHandlerFromCache();
    }

    protected abstract List<Account> getApproverList(String extensions);

    /**
     * 处理工单
     * @param orderDO  工单
     * @param baseDTO  处理工单的基本参数
     * @param userName 用户名
     * @param checkAuthority 检查权限
     * @return ResultStatus
     */
    public ResultStatus handleOrder(OrderDO orderDO,
                                    OrderHandleBaseDTO baseDTO,
                                    String userName,
                                    Boolean checkAuthority) {
        if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(orderDO.getStatus())) {
            return ResultStatus.ORDER_ALREADY_HANDLED;
        }
        if (checkAuthority) {
            ResultStatus authorityStatus = checkAuthority(orderDO, userName);
            if (!ResultStatus.SUCCESS.equals(authorityStatus)) {
                return authorityStatus;
            }
        }
        if (OrderStatusEnum.REFUSED.getCode().equals(baseDTO.getStatus())) {
            ResultStatus resultStatus = updateOrder(orderDO, baseDTO, userName);
            if (!ResultStatus.SUCCESS.equals(resultStatus)) {
                return resultStatus;
            }
//            SpringTool.publish(new OrderRefusedEvent(this, orderDO, configUtils.getIdc(), getApproverList(orderDO)));
            return resultStatus;
        }
        ResultStatus result = handleOrderDetail(orderDO, baseDTO, userName);
        if (ResultStatus.SUCCESS.equals(result)) {
            result = updateOrder(orderDO, baseDTO, userName);
            if (!ResultStatus.SUCCESS.equals(result)) {
                return result;
            }
//            SpringTool.publish(new OrderPassedEvent(this, orderDO, configUtils.getIdc(), getApproverList(orderDO)));
            return result;
        }
        return result;
    }

    protected abstract ResultStatus checkAuthority(OrderDO orderDO, String userName);

    protected abstract ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO baseDTO, String userName);

    public ResultStatus updateOrder(OrderDO orderDO, OrderHandleBaseDTO baseDTO, String userName) {
        orderDO.setApprover(userName);
        orderDO.setOpinion(baseDTO.getOpinion());
        orderDO.setStatus(baseDTO.getStatus());
        if (orderService.updateOrderById(orderDO) > 0) {
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.OPERATION_FAILED;
    }
}
