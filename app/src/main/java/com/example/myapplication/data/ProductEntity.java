package com.example.myapplication.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 商品数据库实体。
 */
@Entity(tableName = "products")
public final class ProductEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String categoryId;
    private String name;
    private String specification;
    private int price;
    private int swatchColorResId;
    private int imageResId;
    private boolean hotSale;
    private int salesCount;
    private String productType;
    private boolean active;

    /**
     * 返回商品标识。
     *
     * @return 商品标识
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * 设置商品标识。
     *
     * @param id 商品标识
     */
    public void setId(@NonNull String id) {
        this.id = id;
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
     * 设置分类标识。
     *
     * @param categoryId 分类标识
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
     * 设置商品名称。
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回规格描述。
     *
     * @return 规格描述
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 设置规格描述。
     *
     * @param specification 规格描述
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * 返回售价。
     *
     * @return 售价
     */
    public int getPrice() {
        return price;
    }

    /**
     * 设置售价。
     *
     * @param price 售价
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * 返回色板资源。
     *
     * @return 色板资源
     */
    public int getSwatchColorResId() {
        return swatchColorResId;
    }

    /**
     * 设置色板资源。
     *
     * @param swatchColorResId 色板资源
     */
    public void setSwatchColorResId(int swatchColorResId) {
        this.swatchColorResId = swatchColorResId;
    }

    /**
     * 返回图片资源。
     *
     * @return 图片资源
     */
    public int getImageResId() {
        return imageResId;
    }

    /**
     * 设置图片资源。
     *
     * @param imageResId 图片资源
     */
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    /**
     * 返回是否热销。
     *
     * @return 是否热销
     */
    public boolean isHotSale() {
        return hotSale;
    }

    /**
     * 设置是否热销。
     *
     * @param hotSale 是否热销
     */
    public void setHotSale(boolean hotSale) {
        this.hotSale = hotSale;
    }

    /**
     * 返回销量。
     *
     * @return 销量
     */
    public int getSalesCount() {
        return salesCount;
    }

    /**
     * 设置销量。
     *
     * @param salesCount 销量
     */
    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    /**
     * 返回商品类型。
     *
     * @return 商品类型
     */
    public String getProductType() {
        return productType;
    }

    /**
     * 设置商品类型。
     *
     * @param productType 商品类型
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * 返回是否上架。
     *
     * @return 是否上架
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 设置是否上架。
     *
     * @param active 是否上架
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
