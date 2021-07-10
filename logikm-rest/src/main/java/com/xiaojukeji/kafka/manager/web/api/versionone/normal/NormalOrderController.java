package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.BaseOrderDetailData;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBatchDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderResultVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderTypeVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.OrderVO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.order.detail.OrderDetailBaseVO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.OrderConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/15
 */
@Api(tags = "Normal-工单相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalOrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "工单类型", notes = "")
    @RequestMapping(value = "orders/type-enums", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<OrderTypeVO>> getOrderTypes() {
        List<OrderTypeVO> voList = new ArrayList<>();
        for (OrderTypeEnum elem : OrderTypeEnum.values()) {
            voList.add(new OrderTypeVO(elem.getCode(), elem.getMessage()));
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "工单申请", notes = "")
    @RequestMapping(value = "orders", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderVO> createOrder(@RequestBody OrderDTO dto) {
        dto.setApplicant(SpringTool.getUserName());
        Result result = orderService.createOrder(dto);
        if (!Constant.SUCCESS.equals(result.getCode())) {
            return result;
        }
        return new Result<>(OrderConverter.convert2OrderVO((OrderDO) result.getData()));
    }

    @ApiOperation(value = "工单撤销", notes = "")
    @RequestMapping(value = "orders", method = RequestMethod.DELETE)
    @ResponseBody
    public Result cancelOrder(@RequestParam(value = "id") Long id) {
        return Result.buildFrom(orderService.cancelOrder(id, SpringTool.getUserName()));
    }

    @ApiOperation(value = "工单申请列表", notes = "")
    @RequestMapping(value = "orders", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<OrderVO>> getOrderApplyList(@RequestParam(value = "status") Integer status) {
        return new Result<>(OrderConverter.convert2OrderVOList(
                orderService.getOrderApplyList(SpringTool.getUserName(), status))
        );
    }

    @ApiOperation(value = "工单审核列表", notes = "")
    @RequestMapping(value = "approvals", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<OrderVO>> getOrderApprovalList(@RequestParam(value = "status") Integer status) {
        List<OrderDO> orderDOList = new ArrayList<>();
        String userName = SpringTool.getUserName();
        if (ValidateUtils.isNull(status)) {
            orderDOList = orderService.getApprovalList(userName);
        } else if (OrderStatusEnum.WAIT_DEAL.getCode().equals(status)) {
            orderDOList = orderService.getWaitApprovalList(userName);
        } else if (OrderStatusEnum.PASSED.getCode().equals(status)) {
            orderDOList = orderService.getPassApprovalList(userName);
        }
        return new Result<>(OrderConverter.convert2OrderVOList(orderDOList));
    }

    @ApiOperation(value = "工单详情", notes = "")
    @RequestMapping(value = "orders/{orderId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<OrderDetailBaseVO> getOrderDetail(@PathVariable Long orderId) {
        Result result = orderService.getOrderDetailData(orderId);
        if (!Constant.SUCCESS.equals(result.getCode())) {
            return result;
        }
        return new Result<>(OrderConverter.convert2DetailBaseVO((BaseOrderDetailData) result.getData()));
    }

    @ApiOperation(value = "工单审批", notes = "")
    @RequestMapping(value = "orders", method = RequestMethod.PUT)
    @ResponseBody
    public Result handleOrder(@RequestBody OrderHandleBaseDTO dto) {
        return Result.buildFrom(orderService.handleOrder(dto));
    }

    @ApiOperation(value = "批量审批", notes = "")
    @RequestMapping(value = "orders/batch", method = RequestMethod.PUT)
    @ResponseBody
    public Result<List<OrderResultVO>> handleOrderBatch(@RequestBody OrderHandleBatchDTO dto) {
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return new Result<>(OrderConverter.convert2OrderResultVOList(
                orderService.handleOrderBatch(dto, SpringTool.getUserName())
        ));
    }
}