package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemCategoryBinding;
import com.example.myapplication.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public final class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<ProductCategory> items;
    private final OnCategoryClickListener onCategoryClickListener;
    private String selectedCategoryId;

    public CategoryAdapter(OnCategoryClickListener onCategoryClickListener) {
        this.items = new ArrayList<>();
        this.onCategoryClickListener = onCategoryClickListener;
    }

    /**
     * 提交最新分类数据。
     *
     * @param categories 分类列表
     */
    public void submitList(List<ProductCategory> categories) {
        items.clear();
        items.addAll(categories);
        notifyDataSetChanged();
    }

    /**
     * 更新当前高亮分类。
     *
     * @param categoryId 分类标识
     */
    public void setSelectedCategoryId(String categoryId) {
        selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    /**
     * 创建分类项视图。
     *
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 分类项视图持有者
     */
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CategoryViewHolder(binding);
    }

    /**
     * 绑定分类项数据。
     *
     * @param holder 视图持有者
     * @param position 列表位置
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ProductCategory category = items.get(position);
        boolean selected = category.getId().equals(selectedCategoryId);
        holder.binding.categoryNameText.setText(category.getName());
        holder.binding.categoryCountText.setText(String.valueOf(category.getProductCount()));
        holder.binding.categoryBadgeText.setText(category.isRecommended() ? "🔥" : "");
        holder.binding.categoryBadgeText.setVisibility(category.isRecommended() ? View.VISIBLE : View.GONE);
        holder.binding.categoryContainer.setCardBackgroundColor(
                ContextCompat.getColor(
                        holder.binding.getRoot().getContext(),
                        selected ? R.color.category_selected_background : R.color.white
                )
        );
        holder.binding.categoryContainer.setStrokeColor(
                ContextCompat.getColor(
                        holder.binding.getRoot().getContext(),
                        selected ? R.color.price_highlight : R.color.divider_color
                )
        );
        holder.binding.categoryNameText.setTextColor(
                ContextCompat.getColor(
                        holder.binding.getRoot().getContext(),
                        selected ? R.color.price_highlight : R.color.primary_text
                )
        );
        holder.binding.getRoot().setOnClickListener(view -> {
            selectedCategoryId = category.getId();
            notifyDataSetChanged();
            onCategoryClickListener.onCategoryClick(category);
        });
    }

    /**
     * 返回分类数量。
     *
     * @return 分类数量
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnCategoryClickListener {

        /**
         * 处理分类点击事件。
         *
         * @param category 分类信息
         */
        void onCategoryClick(ProductCategory category);
    }

    static final class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryBinding binding;

        CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
