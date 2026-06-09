package com.example.myapplication.util;

import java.util.Locale;

/**
 * 金额格式化工具。
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    /**
     * 按人民币样式格式化整数金额。
     *
     * @param amount 金额
     * @return 格式化后的金额字符串
     */
    public static String format(int amount) {
        return String.format(Locale.CHINA, "¥%d", amount);
    }
}
