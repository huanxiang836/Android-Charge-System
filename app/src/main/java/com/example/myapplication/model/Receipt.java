package com.example.myapplication.model;

import java.util.List;

/**
 * 小票模型。
 */
public final class Receipt {

    private final String orderNo;
    private final String createTime;
    private final List<OrderItem> items;
    private final OrderSummary orderSummary;
    private final PaymentType paymentType;
    private final int receivedAmount;
    private final int changeAmount;

    public Receipt(
            String orderNo,
            String createTime,
            List<OrderItem> items,
            OrderSummary orderSummary,
            PaymentType paymentType,
            int receivedAmount,
            int changeAmount
    ) {
        this.orderNo = orderNo;
        this.createTime = createTime;
        this.items = items;
        this.orderSummary = orderSummary;
        this.paymentType = paymentType;
        this.receivedAmount = receivedAmount;
        this.changeAmount = changeAmount;
    }

    /**
     * 返回订单号。
     *
     * @return 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 返回下单时间。
     *
     * @return 下单时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 返回小票明细。
     *
     * @return 小票明细
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * 返回订单汇总。
     *
     * @return 订单汇总
     */
    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    /**
     * 返回支付方式。
     *
     * @return 支付方式
     */
    public PaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * 返回实收金额。
     *
     * @return 实收金额
     */
    public int getReceivedAmount() {
        return receivedAmount;
    }

    /**
     * 返回找零金额。
     *
     * @return 找零金额
     */
    public int getChangeAmount() {
        return changeAmount;
    }
}
