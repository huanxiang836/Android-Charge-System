package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemManageProductBinding;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 管理页商品适配器。
 */
public final class ManageProductAdapter extends RecyclerView.Adapter<ManageProductAdapter.ManageProductViewHolder> {

    private final List<Product> items;
    private final Map<String, String> categoryNameMap;
    private final OnManageProductActionListener onManageProductActionListener;

    public ManageProductAdapter(
            Map<String, String> categoryNameMap,
            OnManageProductActionListener onManageProductActionListener
    ) {
        this.items = new ArrayList<>();
        this.categoryNameMap = categoryNameMap;
        this.onManageProductActionListener = onManageProductActionListener;
    }

    /**
     * 提交商品列表。
     *
     * @param products 商品列表
     */
    public void submitList(List<Product> products) {
        items.clear();
        items.addAll(products);
        notifyDataSetChanged();
    }

    /**
     * 创建管理项视图。
     *
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 视图持有者
     */
    @NonNull
    @Override
    public ManageProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemManageProductBinding binding = ItemManageProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ManageProductViewHolder(binding);
    }

    /**
     * 绑定管理项数据。
     *
     * @param holder 视图持有者
     * @param position 列表位置
     */
    @Override
    public void onBindViewHolder(@NonNull ManageProductViewHolder holder, int position) {
        Product product = items.get(position);
        String categoryName = categoryNameMap.containsKey(product.getCategoryId())
                ? categoryNameMap.get(product.getCategoryId())
                : product.getCategoryId();
        holder.binding.manageProductNameText.setText(product.getName());
        if (product.isCombo()) {
            holder.binding.manageProductMetaText.setText(
                    holder.binding.getRoot().getContext().getString(
                            R.string.manage_combo_default_meta,
                            CurrencyUtils.format(product.getPrice())
                    ) + " · " + holder.binding.getRoot().getContext().getString(
                            R.string.manage_combo_contains_format,
                            product.getComboItemCount()
                    )
            );
            holder.binding.manageProductEditButton.setVisibility(android.view.View.VISIBLE);
            holder.binding.manageProductEditButton.setOnClickListener(view ->
                    onManageProductActionListener.onEdit(product));
        } else {
            holder.binding.manageProductMetaText.setText(
                    categoryName + " · " + CurrencyUtils.format(product.getPrice())
            );
            holder.binding.manageProductEditButton.setVisibility(android.view.View.GONE);
        }
        boolean active = product.isActive();
        holder.binding.manageProductStatusText.setText(
                active
                        ? holder.binding.getRoot().getContext().getString(R.string.manage_status_active)
                        : holder.binding.getRoot().getContext().getString(R.string.manage_status_inactive)
        );
        holder.binding.manageProductActionButton.setText(
                active
                        ? holder.binding.getRoot().getContext().getString(R.string.manage_action_disable)
                        : holder.binding.getRoot().getContext().getString(R.string.manage_action_enable)
        );
        holder.binding.manageProductActionButton.setBackgroundTintList(
                ContextCompat.getColorStateList(
                        holder.binding.getRoot().getContext(),
                        active ? R.color.price_highlight : R.color.primary_green
                )
        );
        holder.binding.manageProductActionButton.setOnClickListener(view ->
                onManageProductActionListener.onToggle(product));
    }

    /**
     * 返回列表项数量。
     *
     * @return 列表项数量
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnManageProductActionListener {

        /**
         * 切换商品上架状态。
         *
         * @param product 商品信息
         */
        void onToggle(Product product);

        /**
         * 编辑套餐。
         *
         * @param product 套餐信息
         */
        void onEdit(Product product);
    }

    static final class ManageProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemManageProductBinding binding;

        ManageProductViewHolder(ItemManageProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
