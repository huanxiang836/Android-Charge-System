package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品模型。
 */
public final class Product {

    private final String id;
    private final String categoryId;
    private final String name;
    private final String specification;
    private final int price;
    private final int swatchColorResId;
    private final int imageResId;
    private final boolean isHotSale;
    private final int salesCount;
    private final ProductType productType;
    private final List<ComboItem> comboItems;
    private boolean active;

    public Product(
            String id,
            String categoryId,
            String name,
            String specification,
            int price,
            int swatchColorResId,
            int imageResId,
            boolean isHotSale,
            int salesCount
    ) {
        this(id, categoryId, name, specification, price, swatchColorResId, imageResId, isHotSale, salesCount,
                ProductType.NORMAL, new ArrayList<>(), true);
    }

    public Product(
            String id,
            String categoryId,
            String name,
            String specification,
            int price,
            int swatchColorResId,
            int imageResId,
            boolean isHotSale,
            int salesCount,
            boolean active
    ) {
        this(id, categoryId, name, specification, price, swatchColorResId, imageResId, isHotSale, salesCount,
                ProductType.NORMAL, new ArrayList<>(), active);
    }

    public Product(
            String id,
            String categoryId,
            String name,
            String specification,
            int price,
            int swatchColorResId,
            int imageResId,
            boolean isHotSale,
            int salesCount,
            ProductType productType,
            List<ComboItem> comboItems,
            boolean active
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.specification = specification;
        this.price = price;
        this.swatchColorResId = swatchColorResId;
        this.imageResId = imageResId;
        this.isHotSale = isHotSale;
        this.salesCount = salesCount;
        this.productType = productType;
        this.comboItems = new ArrayList<>(comboItems);
        this.active = active;
    }

    /**
     * 返回商品唯一标识。
     *
     * @return 商品唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * 返回分类标识。
     *
     * @return 分类标识
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * 返回商品名称。
     *
     * @return 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 返回默认规格描述。
     *
     * @return 规格描述
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 返回商品单价，单位为元。
     *
     * @return 商品单价
     */
    public int getPrice() {
        return price;
    }

    /**
     * 返回商品色板资源。
     *
     * @return 颜色资源标识
     */
    public int getSwatchColorResId() {
        return swatchColorResId;
    }

    /**
     * 返回商品图片资源。
     *
     * @return 图片资源标识
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * 返回是否热销商品。
     *
     * @return 是否热销
     */
    public boolean isHotSale() {
        return isHotSale;
    }

    /**
     * 返回历史销量。
     *
     * @return 销量
     */
    public int getSalesCount() {
        return salesCount;
    }

    /**
     * 返回商品类型。
     *
     * @return 商品类型
     */
    public ProductType getProductType() {
        return productType;
    }

    /**
     * 返回套餐子项。
     *
     * @return 套餐子项
     */
    public List<ComboItem> getComboItems() {
        return new ArrayList<>(comboItems);
    }

    /**
     * 返回是否套餐。
     *
     * @return true 表示套餐
     */
    public boolean isCombo() {
        return productType == ProductType.COMBO;
    }

    /**
     * 返回套餐种类数量。
     *
     * @return 套餐种类数量
     */
    public int getComboItemCount() {
        return comboItems.size();
    }

    /**
     * 返回套餐摘要。
     *
     * @return 套餐摘要
     */
    public String getComboSummary() {
        if (comboItems.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < comboItems.size(); i++) {
            if (i > 0) {
                builder.append("、");
            }
            builder.append(comboItems.get(i).toSummaryText());
        }
        return builder.toString();
    }

    /**
     * 返回是否上架。
     *
     * @return true 表示上架
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 更新上架状态。
     *
     * @param active 新状态
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
