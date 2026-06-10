package com.example.myapplication.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * 套餐子项数据库实体。
 */
@Entity(tableName = "combo_items", primaryKeys = {"comboProductId", "childProductId"})
public final class ComboItemEntity {

    @NonNull
    private String comboProductId;
    @NonNull
    private String childProductId;
    private String childProductNameSnapshot;
    private int quantity;

    /**
     * 返回套餐商品标识。
     *
     * @return 套餐商品标识
     */
    @NonNull
    public String getComboProductId() {
        return comboProductId;
    }

    /**
     * 设置套餐商品标识。
     *
     * @param comboProductId 套餐商品标识
     */
    public void setComboProductId(@NonNull String comboProductId) {
        this.comboProductId = comboProductId;
    }

    /**
     * 返回子商品标识。
     *
     * @return 子商品标识
     */
    @NonNull
    public String getChildProductId() {
        return childProductId;
    }

    /**
     * 设置子商品标识。
     *
     * @param childProductId 子商品标识
     */
    public void setChildProductId(@NonNull String childProductId) {
        this.childProductId = childProductId;
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
     * 设置子商品名称快照。
     *
     * @param childProductNameSnapshot 子商品名称快照
     */
    public void setChildProductNameSnapshot(String childProductNameSnapshot) {
        this.childProductNameSnapshot = childProductNameSnapshot;
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
     * 设置子商品数量。
     *
     * @param quantity 子商品数量
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
