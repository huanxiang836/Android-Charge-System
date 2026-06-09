package com.example.myapplication.model;

/**
 * 订单明细模型。
 */
public final class OrderItem {

    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
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
