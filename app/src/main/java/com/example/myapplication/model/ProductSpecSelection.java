package com.example.myapplication.model;

/**
 * 商品规格选择结果。
 */
public final class ProductSpecSelection {

    private final String size;
    private final String temperature;
    private final String sweetness;
    private final String displayText;
    private final String key;

    public ProductSpecSelection(String size, String temperature, String sweetness) {
        this.size = size;
        this.temperature = temperature;
        this.sweetness = sweetness;
        this.displayText = size + "/" + temperature + "/" + sweetness;
        this.key = size + "|" + temperature + "|" + sweetness;
    }

    private ProductSpecSelection(String size, String temperature, String sweetness, String displayText, String key) {
        this.size = size;
        this.temperature = temperature;
        this.sweetness = sweetness;
        this.displayText = displayText;
        this.key = key;
    }

    /**
     * 创建套餐选择结果。
     *
     * @param comboSummary 套餐摘要
     * @return 规格选择结果
     */
    public static ProductSpecSelection createComboSelection(String comboSummary) {
        return new ProductSpecSelection(
                "套餐",
                "套餐",
                "套餐",
                "套餐内容：" + comboSummary,
                "combo|" + comboSummary
        );
    }

    /**
     * 返回杯型。
     *
     * @return 杯型
     */
    public String getSize() {
        return size;
    }

    /**
     * 返回温度。
     *
     * @return 温度
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * 返回甜度。
     *
     * @return 甜度
     */
    public String getSweetness() {
        return sweetness;
    }

    /**
     * 返回用于订单展示的规格文案。
     *
     * @return 规格文案
     */
    public String toDisplayText() {
        return displayText;
    }

    /**
     * 返回用于订单合并的规格键。
     *
     * @return 规格键
     */
    public String toKey() {
        return key;
    }
}
