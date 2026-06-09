package com.example.myapplication.model;

/**
 * 支付方式枚举。
 */
public enum PaymentType {
    WECHAT("微信支付"),
    ALIPAY("支付宝支付"),
    MEMBER("会员支付"),
    CASH("现金支付");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 返回支付方式显示文案。
     *
     * @return 支付方式显示文案
     */
    public String getDisplayName() {
        return displayName;
    }
}
