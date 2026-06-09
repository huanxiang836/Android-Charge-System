package com.example.myapplication.model;

/**
 * 订单汇总模型。
 */
public final class OrderSummary {

    private final int itemCount;
    private final int subtotal;
    private final int discount;
    private final int payableAmount;

    public OrderSummary(int itemCount, int subtotal, int discount, int payableAmount) {
        this.itemCount = itemCount;
        this.subtotal = subtotal;
        this.discount = discount;
        this.payableAmount = payableAmount;
    }

    /**
     * 返回商品总数量。
     *
     * @return 商品总数量
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * 返回订单小计。
     *
     * @return 订单小计
     */
    public int getSubtotal() {
        return subtotal;
    }

    /**
     * 返回优惠金额。
     *
     * @return 优惠金额
     */
    public int getDiscount() {
        return discount;
    }

    /**
     * 返回应收金额。
     *
     * @return 应收金额
     */
    public int getPayableAmount() {
        return payableAmount;
    }
}
