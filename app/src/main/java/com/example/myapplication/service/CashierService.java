package com.example.myapplication.service;

import com.example.myapplication.model.OrderItem;
import com.example.myapplication.model.OrderSummary;
import com.example.myapplication.model.PaymentType;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;
import com.example.myapplication.model.ProductSpecSelection;
import com.example.myapplication.model.Receipt;
import com.example.myapplication.model.ComboItem;
import com.example.myapplication.model.Member;
import com.example.myapplication.model.MemberOperationConfig;
import com.example.myapplication.util.DiscountUtils;

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
    private final MemberService memberService;
    private final Map<String, OrderItem> currentItems;
    private final AtomicInteger orderSequence;
    private Receipt latestReceipt;
    private String orderStatusText;
    private Map<String, OrderItem> heldOrderItems;
    private String heldOrderStatusText;
    private Member currentMember;
    private Member heldMember;

    public CashierService(StoreCatalogService storeCatalogService, MemberService memberService) {
        this.storeCatalogService = storeCatalogService;
        this.memberService = memberService;
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
     * 返回管理页商品列表。
     *
     * @return 商品列表
     */
    public List<Product> getManageProducts() {
        return storeCatalogService.getManageProducts();
    }

    /**
     * 返回单个管理商品。
     *
     * @param productId 商品标识
     * @return 商品信息
     */
    public Product getManageProduct(String productId) {
        return storeCatalogService.getManageProduct(productId);
    }

    /**
     * 返回分类名称映射。
     *
     * @return 分类名称映射
     */
    public Map<String, String> getCategoryNameMap() {
        return storeCatalogService.getCategoryNameMap();
    }

    /**
     * 返回套餐候选单品。
     *
     * @return 候选单品
     */
    public List<Product> getComboCandidateProducts() {
        return storeCatalogService.getComboCandidateProducts();
    }

    /**
     * 新增普通商品。
     *
     * @param categoryId 分类标识
     * @param name 商品名称
     * @param price 商品价格
     */
    public void addManageNormalProduct(String categoryId, String name, int price) {
        storeCatalogService.addNormalProduct(categoryId, name, price);
    }

    /**
     * 新增套餐商品。
     *
     * @param name 套餐名称
     * @param price 套餐价格
     * @param comboItems 套餐子项
     */
    public void addManageComboProduct(String name, int price, List<ComboItem> comboItems) {
        storeCatalogService.addComboProduct(name, price, comboItems);
    }

    /**
     * 更新套餐商品。
     *
     * @param productId 商品标识
     * @param name 套餐名称
     * @param price 套餐价格
     * @param comboItems 套餐子项
     */
    public void updateManageComboProduct(String productId, String name, int price, List<ComboItem> comboItems) {
        storeCatalogService.updateComboProduct(productId, name, price, comboItems);
    }

    /**
     * 更新商品上架状态。
     *
     * @param productId 商品标识
     * @param active 是否上架
     */
    public void updateManageProductActive(String productId, boolean active) {
        storeCatalogService.updateProductActive(productId, active);
        if (!active) {
            removeInactiveItemsFromCurrentOrder(productId);
        }
    }

    /**
     * 向当前订单添加商品。
     *
     * @param product 商品信息
     */
    public void addProduct(Product product, ProductSpecSelection specSelection) {
        String orderItemKey = buildOrderItemKey(product, specSelection);
        OrderItem existingItem = currentItems.get(orderItemKey);
        if (existingItem == null) {
            currentItems.put(orderItemKey, new OrderItem(
                    orderItemKey,
                    product,
                    specSelection.toDisplayText(),
                    1
            ));
        } else {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        }
        orderStatusText = "待结算";
    }

    /**
     * 绑定当前订单会员。
     *
     * @param member 会员信息
     */
    public void bindCurrentMember(Member member) {
        currentMember = member;
    }

    /**
     * 取消当前订单会员。
     */
    public void clearCurrentMember() {
        currentMember = null;
    }

    /**
     * 返回当前订单会员。
     *
     * @return 当前订单会员
     */
    public Member getCurrentMember() {
        return currentMember;
    }

    /**
     * 返回会员运营配置。
     *
     * @return 会员运营配置
     */
    public MemberOperationConfig getMemberOperationConfig() {
        return memberService.getOperationConfig();
    }

    /**
     * 修改商品数量。
     *
     * @param productId 商品标识
     * @param delta 数量变化
     */
    public void changeQuantity(String orderItemKey, int delta) {
        OrderItem orderItem = currentItems.get(orderItemKey);
        if (orderItem == null) {
            return;
        }
        int newQuantity = orderItem.getQuantity() + delta;
        if (newQuantity <= 0) {
            currentItems.remove(orderItemKey);
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
    public void removeProduct(String orderItemKey) {
        currentItems.remove(orderItemKey);
        if (currentItems.isEmpty()) {
            orderStatusText = "待结算";
        }
    }

    /**
     * 清空当前订单。
     */
    public void clearOrder() {
        currentItems.clear();
        currentMember = null;
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
            heldOrderItems.put(entry.getKey(), new OrderItem(
                    item.getOrderItemKey(),
                    item.getProduct(),
                    item.getSpecification(),
                    item.getQuantity()
            ));
        }
        heldOrderStatusText = "已挂起 " + getOrderSummary().getItemCount() + " 件商品";
        heldMember = currentMember;
        currentItems.clear();
        currentMember = null;
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
        currentMember = heldMember;
        heldOrderItems = null;
        heldOrderStatusText = null;
        heldMember = null;
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
        int promotionDiscount = itemCount >= PROMOTION_THRESHOLD ? PROMOTION_DISCOUNT : 0;
        int afterPromotionAmount = Math.max(0, subtotal - promotionDiscount);
        int memberDiscount = currentMember == null
                ? 0
                : Math.max(0, afterPromotionAmount * (100 - currentMember.getDiscountRate()) / 100);
        int totalDiscount = promotionDiscount + memberDiscount;
        String memberDisplayText = currentMember == null
                ? "当前订单未使用会员"
                : currentMember.getName() + " · " + maskPhone(currentMember.getPhone())
                + " · " + DiscountUtils.formatRate(currentMember.getDiscountRate());
        return new OrderSummary(
                itemCount,
                subtotal,
                promotionDiscount,
                memberDiscount,
                totalDiscount,
                Math.max(0, subtotal - totalDiscount),
                currentMember != null,
                memberDisplayText
        );
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
            snapshotItems.add(new OrderItem(
                    orderItem.getOrderItemKey(),
                    orderItem.getProduct(),
                    orderItem.getSpecification(),
                    orderItem.getQuantity()
            ));
        }
        OrderSummary orderSummary = getOrderSummary();
        String orderNo = String.format(Locale.CHINA, "MT%s%03d", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()), orderSequence.getAndIncrement());
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        int changeAmount = Math.max(0, receivedAmount - orderSummary.getPayableAmount());
        latestReceipt = new Receipt(
                orderNo,
                createTime,
                snapshotItems,
                orderSummary,
                paymentType,
                receivedAmount,
                changeAmount,
                currentMember == null ? null : currentMember.getName(),
                currentMember == null ? null : currentMember.getPhone(),
                currentMember == null ? 0 : currentMember.getDiscountRate(),
                orderSummary.getMemberDiscount()
        );
        if (currentMember != null) {
            memberService.recordMemberOrder(orderNo, createTime, currentMember, orderSummary.getMemberDiscount(), paymentType);
        }
        orderStatusText = paymentType.getDisplayName() + " 已完成";
        currentItems.clear();
        currentMember = null;
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

    private String buildOrderItemKey(Product product, ProductSpecSelection specSelection) {
        return product.getId() + "#" + specSelection.toKey();
    }

    private void removeInactiveItemsFromCurrentOrder(String productId) {
        Map<String, OrderItem> filteredItems = new LinkedHashMap<>();
        for (Map.Entry<String, OrderItem> entry : currentItems.entrySet()) {
            if (!entry.getValue().getProduct().getId().equals(productId)) {
                filteredItems.put(entry.getKey(), entry.getValue());
            }
        }
        currentItems.clear();
        currentItems.putAll(filteredItems);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
