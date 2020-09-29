package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.bpm.common.OrderResult;
import com.xiaojukeji.kafka.manager.bpm.common.entry.BaseOrderDetailData;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderResultVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.detail.OrderDetailBaseVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/18
 */
public class OrderConverter {
    public static List<OrderVO> convert2OrderVOList(List<OrderDO> orderDOList) {
        if (orderDOList == null || orderDOList.isEmpty()) {
            return new ArrayList<>();
        }
        List<OrderVO> orderVOList = new ArrayList<>();
        for (OrderDO orderDO : orderDOList) {
            OrderVO orderVO = convert2OrderVO(orderDO);
            if (ValidateUtils.isNull(orderVO)) {
                continue;
            }
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    public static OrderVO convert2OrderVO(OrderDO orderDO) {
        if (ValidateUtils.isNull(orderDO)) {
            return null;
        }
        OrderVO orderVO = new OrderVO();
        CopyUtils.copyProperties(orderVO, orderDO);
        orderVO.setGmtTime(orderDO.getGmtCreate());
        return orderVO;
    }

    public static OrderDetailBaseVO convert2DetailBaseVO(BaseOrderDetailData baseDTO) {
        OrderDetailBaseVO baseVO = new OrderDetailBaseVO();
        if (ValidateUtils.isNull(baseDTO)) {
            return baseVO;
        }
        CopyUtils.copyProperties(baseVO, baseDTO);
        baseVO.setDetail(baseDTO.getDetail());
        AccountVO accountVO = new AccountVO();
        CopyUtils.copyProperties(accountVO, baseDTO.getApplicant());
        if (!ValidateUtils.isNull(baseDTO.getApplicant()) &&
                !ValidateUtils.isNull(baseDTO.getApplicant().getAccountRoleEnum())) {
            accountVO.setRole(baseDTO.getApplicant().getAccountRoleEnum().getRole());
        }
        baseVO.setApplicant(accountVO);
        ArrayList<AccountVO> approverList = new ArrayList<>();
        for (Account account : baseDTO.getApproverList()) {
            AccountVO approver = new AccountVO();
            CopyUtils.copyProperties(approver, account);
            if (!ValidateUtils.isNull(account.getAccountRoleEnum())) {
                approver.setRole(account.getAccountRoleEnum().getRole());
            }
            approverList.add(approver);
        }
        baseVO.setApproverList(approverList);
        return baseVO;
    }

    public static List<OrderResultVO> convert2OrderResultVOList(List<OrderResult> orderResultList) {
        if (ValidateUtils.isEmptyList(orderResultList)) {
            return Collections.emptyList();
        }
        List<OrderResultVO> voList = new ArrayList<>();
        for (OrderResult orderResult : orderResultList) {
            OrderResultVO vo = new OrderResultVO();
            vo.setId(orderResult.getId());
            vo.setResult(orderResult.getResult());
            voList.add(vo);
        }
        return voList;
    }
}