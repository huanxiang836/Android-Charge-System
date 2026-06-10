package com.example.myapplication.model;

/**
 * 套餐子项模型。
 */
public final class ComboItem {

    private final String childProductId;
    private final String childProductNameSnapshot;
    private final int quantity;

    public ComboItem(String childProductId, String childProductNameSnapshot, int quantity) {
        this.childProductId = childProductId;
        this.childProductNameSnapshot = childProductNameSnapshot;
        this.quantity = quantity;
    }

    /**
     * 返回子商品标识。
     *
     * @return 子商品标识
     */
    public String getChildProductId() {
        return childProductId;
    }

    /**
     * 返回子商品名称快照。
     *
     * @return 子商品名称快照
     */
    public String getChildProductNameSnapshot() {
        return childProductNameSnapshot;
    }

    /**
     * 返回子商品数量。
     *
     * @return 子商品数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 返回子项摘要。
     *
     * @return 子项摘要
     */
    public String toSummaryText() {
        return childProductNameSnapshot + "x" + quantity;
    }
}
