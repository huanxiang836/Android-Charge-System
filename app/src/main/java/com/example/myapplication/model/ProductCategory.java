package com.example.myapplication.model;

/**
 * 商品分类视图模型。
 */
public final class ProductCategory {

    private final String id;
    private final String name;
    private final int productCount;
    private final boolean recommended;

    public ProductCategory(String id, String name, int productCount, boolean recommended) {
        this.id = id;
        this.name = name;
        this.productCount = productCount;
        this.recommended = recommended;
    }

    /**
     * 返回分类唯一标识。
     *
     * @return 分类唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * 返回分类名称。
     *
     * @return 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 返回分类下商品数量。
     *
     * @return 商品数量
     */
    public int getProductCount() {
        return productCount;
    }

    /**
     * 返回是否为推荐分类。
     *
     * @return true 表示推荐分类
     */
    public boolean isRecommended() {
        return recommended;
    }
}
