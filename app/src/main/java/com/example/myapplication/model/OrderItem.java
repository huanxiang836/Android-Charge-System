package com.example.myapplication.model;

/**
 * 订单明细模型。
 */
public final class OrderItem {

    private final String orderItemKey;
    private final Product product;
    private final String specification;
    private int quantity;

    public OrderItem(String orderItemKey, Product product, String specification, int quantity) {
        this.orderItemKey = orderItemKey;
        this.product = product;
        this.specification = specification;
        this.quantity = quantity;
    }

    /**
     * 返回订单项唯一标识。
     *
     * @return 订单项唯一标识
     */
    public String getOrderItemKey() {
        return orderItemKey;
    }

    /**
     * 返回商品信息。
     *
     * @return 商品信息
     */
    public Product getProduct() {
        return product;
    }

    /**
     * 返回当前规格文案。
     *
     * @return 规格文案
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 返回当前数量。
     *
     * @return 当前数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 更新当前数量。
     *
     * @param quantity 新数量
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 返回明细金额。
     *
     * @return 商品总金额
     */
    public int getLineAmount() {
        return product.getPrice() * quantity;
    }
}
