package com.xiaojukeji.kafka.manager.notify.common;

/**
 * @author zengqiao
 * @date 20/09/03
 */
public class OrderNotifyTemplate {
    private static String NOTIFY_HANDLER_TEMPLATE = "%s，您好！您收到来自%s的申请工单：“%s”请及时审批。如需查看详情，请前往 %s";
    public static String getNotify2OrderHandlerMessage(String username,
                                                       String applicant,
                                                       String title,
                                                       String orderDetailUrl) {
        return String.format(
                NOTIFY_HANDLER_TEMPLATE,
                username,
                applicant,
                title,
                orderDetailUrl
        );
    }

    private static String NOTIFY_ORDER_PASSED_TEMPLATE = "%s，您好！您的申请工单：“%s”已完成审批，结果为：通过。如需查看详情，请前往 %s";
    public static String getNotifyOrderPassed2ApplicantMessage(String username,
                                                               String title,
                                                               String orderDetailUrl) {
        return String.format(NOTIFY_ORDER_PASSED_TEMPLATE,
                username,
                title,
                orderDetailUrl
        );
    }

    private static String NOTIFY_ORDER_REFUSED_TEMPLATE = "%s，您好！您的申请工单：“%s”已完成审批，结果为：拒绝。如需查看详情，请前往 %s";
    public static String getNotifyOrderRefused2ApplicantMessage(String username,
                                                                String title,
                                                                String orderDetailUrl) {
        return String.format(NOTIFY_ORDER_REFUSED_TEMPLATE,
                username,
                title,
                orderDetailUrl
        );
    }
}
