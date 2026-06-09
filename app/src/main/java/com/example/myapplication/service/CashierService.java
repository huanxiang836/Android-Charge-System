package com.example.myapplication.service;

import com.example.myapplication.model.OrderItem;
import com.example.myapplication.model.OrderSummary;
import com.example.myapplication.model.PaymentType;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;
import com.example.myapplication.model.Receipt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 收银业务服务。
 */
public final class CashierService {

    private static final int PROMOTION_THRESHOLD = 3;
    private static final int PROMOTION_DISCOUNT = 5;

    private final StoreCatalogService storeCatalogService;
    private final Map<String, OrderItem> currentItems;
    private final AtomicInteger orderSequence;
    private Receipt latestReceipt;
    private String orderStatusText;
    private Map<String, OrderItem> heldOrderItems;
    private String heldOrderStatusText;

    public CashierService(StoreCatalogService storeCatalogService) {
        this.storeCatalogService = storeCatalogService;
        currentItems = new LinkedHashMap<>();
        orderSequence = new AtomicInteger(1);
        orderStatusText = "待结算";
        heldOrderItems = null;
        heldOrderStatusText = null;
    }

    /**
     * 返回分类列表。
     *
     * @return 分类列表
     */
    public List<ProductCategory> getCategories() {
        return storeCatalogService.getCategories();
    }

    /**
     * 返回商品列表。
     *
     * @param categoryId 分类标识
     * @param keyword 关键字
     * @return 商品列表
     */
    public List<Product> getProducts(String categoryId, String keyword) {
        return storeCatalogService.getProducts(categoryId, keyword);
    }

    /**
     * 向当前订单添加商品。
     *
     * @param product 商品信息
     */
    public void addProduct(Product product) {
        OrderItem existingItem = currentItems.get(product.getId());
        if (existingItem == null) {
            currentItems.put(product.getId(), new OrderItem(product, 1));
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        }
        orderStatusText = "待结算";
    }

    /**
     * 修改商品数量。
     *
     * @param productId 商品标识
     * @param delta 数量变化
     */
    public void changeQuantity(String productId, int delta) {
        OrderItem orderItem = currentItems.get(productId);
        if (orderItem == null) {
            return;
        }
        int newQuantity = orderItem.getQuantity() + delta;
        if (newQuantity <= 0) {
            currentItems.remove(productId);
        } else {
            orderItem.setQuantity(newQuantity);
        }
        orderStatusText = currentItems.isEmpty() ? "待结算" : orderStatusText;
    }

    /**
     * 删除订单商品。
     *
     * @param productId 商品标识
     */
    public void removeProduct(String productId) {
        currentItems.remove(productId);
        if (currentItems.isEmpty()) {
            orderStatusText = "待结算";
        }
    }

    /**
     * 清空当前订单。
     */
    public void clearOrder() {
        currentItems.clear();
        orderStatusText = "待结算";
    }

    /**
     * 挂起当前订单。
     */
    public void holdCurrentOrder() {
        if (currentItems.isEmpty()) {
            return;
        }
        heldOrderItems = new LinkedHashMap<>();
        for (Map.Entry<String, OrderItem> entry : currentItems.entrySet()) {
            OrderItem item = entry.getValue();
            heldOrderItems.put(entry.getKey(), new OrderItem(item.getProduct(), item.getQuantity()));
        }
        heldOrderStatusText = "已挂起 " + getOrderSummary().getItemCount() + " 件商品";
        currentItems.clear();
        orderStatusText = "订单已挂起";
    }

    /**
     * 恢复已挂起的订单。
     */
    public void resumeHeldOrder() {
        if (heldOrderItems == null || heldOrderItems.isEmpty()) {
            return;
        }
        currentItems.clear();
        currentItems.putAll(heldOrderItems);
        heldOrderItems = null;
        heldOrderStatusText = null;
        orderStatusText = "待结算";
    }

    /**
     * 返回是否有挂起的订单。
     *
     * @return 是否有挂起订单
     */
    public boolean hasHeldOrder() {
        return heldOrderItems != null && !heldOrderItems.isEmpty();
    }

    /**
     * 返回挂起订单状态文案。
     *
     * @return 挂起订单状态文案
     */
    public String getHeldOrderStatusText() {
        return heldOrderStatusText;
    }

    /**
     * 返回当前订单明细。
     *
     * @return 当前订单明细
     */
    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(currentItems.values());
    }

    /**
     * 返回订单汇总。
     *
     * @return 订单汇总
     */
    public OrderSummary getOrderSummary() {
        int itemCount = 0;
        int subtotal = 0;
        for (OrderItem orderItem : currentItems.values()) {
            itemCount += orderItem.getQuantity();
            subtotal += orderItem.getLineAmount();
        }
        int discount = itemCount >= PROMOTION_THRESHOLD ? PROMOTION_DISCOUNT : 0;
        return new OrderSummary(itemCount, subtotal, discount, Math.max(0, subtotal - discount));
    }

    /**
     * 返回当前订单的应付金额。
     *
     * @return 应付金额
     */
    public int getPayableAmount() {
        return getOrderSummary().getPayableAmount();
    }

    /**
     * 执行结算并生成小票（电子支付，实收=应付）。
     *
     * @param paymentType 支付方式
     * @return 新生成的小票
     */
    public Receipt checkout(PaymentType paymentType) {
        return checkout(paymentType, getPayableAmount());
    }

    /**
     * 执行结算并生成小票（支持现金实收金额）。
     *
     * @param paymentType 支付方式
     * @param receivedAmount 实收金额
     * @return 新生成的小票
     */
    public Receipt checkout(PaymentType paymentType, int receivedAmount) {
        List<OrderItem> snapshotItems = new ArrayList<>();
        for (OrderItem orderItem : currentItems.values()) {
            snapshotItems.add(new OrderItem(orderItem.getProduct(), orderItem.getQuantity()));
        }
        OrderSummary orderSummary = getOrderSummary();
        String orderNo = String.format(Locale.CHINA, "MT%s%03d", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()), orderSequence.getAndIncrement());
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        int changeAmount = Math.max(0, receivedAmount - orderSummary.getPayableAmount());
        latestReceipt = new Receipt(orderNo, createTime, snapshotItems, orderSummary, paymentType, receivedAmount, changeAmount);
        orderStatusText = paymentType.getDisplayName() + " 已完成";
        currentItems.clear();
        return latestReceipt;
    }

    /**
     * 返回最近一次小票。
     *
     * @return 最近一次小票
     */
    public Receipt getLatestReceipt() {
        return latestReceipt;
    }

    /**
     * 返回订单状态文案。
     *
     * @return 订单状态文案
     */
    public String getOrderStatusText() {
        return orderStatusText;
    }
}
