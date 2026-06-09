package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemOrderBinding;
import com.example.myapplication.model.OrderItem;
import com.example.myapplication.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public final class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<OrderItem> items;
    private final OnOrderActionListener onIncreaseListener;
    private final OnOrderActionListener onDecreaseListener;
    private final OnOrderActionListener onDeleteListener;

    public OrderAdapter(
            OnOrderActionListener onIncreaseListener,
            OnOrderActionListener onDecreaseListener,
            OnOrderActionListener onDeleteListener
    ) {
        this.items = new ArrayList<>();
        this.onIncreaseListener = onIncreaseListener;
        this.onDecreaseListener = onDecreaseListener;
        this.onDeleteListener = onDeleteListener;
    }

    /**
     * 提交订单明细。
     *
     * @param orderItems 订单明细
     */
    public void submitList(List<OrderItem> orderItems) {
        items.clear();
        items.addAll(orderItems);
        notifyDataSetChanged();
    }

    /**
     * 创建订单项视图。
     *
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 订单项视图持有者
     */
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new OrderViewHolder(binding);
    }

    /**
     * 绑定订单项数据。
     *
     * @param holder 视图持有者
     * @param position 列表位置
     */
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.binding.orderProductNameText.setText(item.getProduct().getName());
        holder.binding.orderProductSpecText.setText(item.getProduct().getSpecification());
        holder.binding.orderProductPriceText.setText(CurrencyUtils.format(item.getProduct().getPrice()));
        holder.binding.quantityValueText.setText(String.valueOf(item.getQuantity()));
        holder.binding.increaseQuantityButton.setOnClickListener(view -> onIncreaseListener.onAction(item));
        holder.binding.decreaseQuantityButton.setOnClickListener(view -> onDecreaseListener.onAction(item));
        holder.binding.deleteItemText.setOnClickListener(view -> onDeleteListener.onAction(item));
    }

    /**
     * 返回订单项数量。
     *
     * @return 订单项数量
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnOrderActionListener {

        /**
         * 处理订单项操作。
         *
         * @param item 订单项
         */
        void onAction(OrderItem item);
    }

    static final class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
