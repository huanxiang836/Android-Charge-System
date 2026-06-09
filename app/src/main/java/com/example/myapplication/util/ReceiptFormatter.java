package com.example.myapplication.util;

import com.example.myapplication.model.OrderItem;
import com.example.myapplication.model.PaymentType;
import com.example.myapplication.model.Receipt;

/**
 * 小票文本格式化工具。
 */
public final class ReceiptFormatter {

    private ReceiptFormatter() {
    }

    /**
     * 生成适合预览和打印的中文小票文本。
     *
     * @param receipt 小票对象
     * @return 小票文本
     */
    public static String format(Receipt receipt) {
        StringBuilder builder = new StringBuilder();
        builder.append("蜜语茶香·中山路店\n");
        builder.append("订单号：").append(receipt.getOrderNo()).append('\n');
        builder.append("下单时间：").append(receipt.getCreateTime()).append('\n');
        builder.append("支付方式：").append(receipt.getPaymentType().getDisplayName()).append("\n\n");
        builder.append("商品明细\n");
        builder.append("--------------------------------\n");
        for (OrderItem item : receipt.getItems()) {
            builder.append(item.getProduct().getName())
                    .append("  x")
                    .append(item.getQuantity())
                    .append("  ")
                    .append(CurrencyUtils.format(item.getLineAmount()))
                    .append('\n');
            builder.append(item.getProduct().getSpecification()).append('\n');
        }
        builder.append("--------------------------------\n");
        builder.append("商品数量：").append(receipt.getOrderSummary().getItemCount()).append('\n');
        builder.append("小计：").append(CurrencyUtils.format(receipt.getOrderSummary().getSubtotal())).append('\n');
        builder.append("优惠：").append(CurrencyUtils.format(receipt.getOrderSummary().getDiscount())).append('\n');
        builder.append("应收：").append(CurrencyUtils.format(receipt.getOrderSummary().getPayableAmount())).append('\n');
        if (receipt.getPaymentType() == PaymentType.CASH) {
            builder.append("实收：").append(CurrencyUtils.format(receipt.getReceivedAmount())).append('\n');
            builder.append("找零：").append(CurrencyUtils.format(receipt.getChangeAmount())).append('\n');
        }
        builder.append('\n');
        builder.append("感谢光临，祝您饮用愉快。");
        return builder.toString();
    }
}
