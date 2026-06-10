package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemProductBinding;
import com.example.myapplication.model.Product;
import com.example.myapplication.util.CurrencyUtils;

import java.util.ArrayList;
import java.util.List;

public final class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> items;
    private final OnProductAddListener onProductAddListener;
    private final OnProductDetailListener onProductDetailListener;
    private boolean listMode;

    public ProductAdapter(OnProductAddListener onProductAddListener,
                          OnProductDetailListener onProductDetailListener) {
        this.items = new ArrayList<>();
        this.onProductAddListener = onProductAddListener;
        this.onProductDetailListener = onProductDetailListener;
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
     * 切换展示模式。
     *
     * @param listMode true 表示列表模式
     */
    public void setListMode(boolean listMode) {
        this.listMode = listMode;
        notifyDataSetChanged();
    }

    /**
     * 创建商品项视图。
     *
     * @param parent 父容器
     * @param viewType 视图类型
     * @return 商品项视图持有者
     */
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ProductViewHolder(binding);
    }

    /**
     * 绑定商品项数据。
     *
     * @param holder 视图持有者
     * @param position 列表位置
     */
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = items.get(position);
        holder.binding.productNameText.setText(product.getName());
        holder.binding.productPriceText.setText(CurrencyUtils.format(product.getPrice()));
        holder.binding.productImagePanel.setBackgroundColor(
                ContextCompat.getColor(holder.binding.getRoot().getContext(), product.getSwatchColorResId())
        );
        holder.binding.productImageView.setImageResource(product.getImageResId());
        holder.binding.productImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.binding.productCard.setStrokeColor(
                ContextCompat.getColor(holder.binding.getRoot().getContext(), R.color.divider_color)
        );
        ViewGroup.LayoutParams params = holder.binding.productImagePanel.getLayoutParams();
        params.height = listMode ? 120 : 152;
        holder.binding.productImagePanel.setLayoutParams(params);

        // 热销徽章
        if (product.isHotSale()) {
            holder.binding.hotSaleBadgeText.setVisibility(View.VISIBLE);
            if (product.getSalesCount() > 0) {
                holder.binding.hotSaleBadgeText.setText(
                        holder.binding.getRoot().getContext().getString(R.string.hot_sale_badge)
                                + " " + product.getSalesCount());
            }
        } else {
            holder.binding.hotSaleBadgeText.setVisibility(View.GONE);
        }

        // 快速添加按钮与卡片点击统一进入规格选择。
        holder.binding.addProductButton.setOnClickListener(view -> onProductAddListener.onProductAdd(product));
        holder.binding.getRoot().setOnClickListener(view -> onProductDetailListener.onProductDetail(product));
    }

    /**
     * 返回商品数量。
     *
     * @return 商品数量
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnProductAddListener {

        /**
         * 处理商品加购事件。
         *
         * @param product 商品信息
         */
        void onProductAdd(Product product);
    }

    public interface OnProductDetailListener {

        /**
         * 处理商品详情点击事件。
         *
         * @param product 商品信息
         */
        void onProductDetail(Product product);
    }

    static final class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
