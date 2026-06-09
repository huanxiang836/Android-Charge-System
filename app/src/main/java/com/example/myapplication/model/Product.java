package com.example.myapplication.model;

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
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.specification = specification;
        this.price = price;
        this.swatchColorResId = swatchColorResId;
        this.imageResId = imageResId;
        this.isHotSale = isHotSale;
        this.salesCount = salesCount;
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
}
