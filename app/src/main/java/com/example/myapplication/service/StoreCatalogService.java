package com.example.myapplication.service;

import android.content.Context;

import com.example.myapplication.R;
import com.example.myapplication.data.CatalogDatabaseSeeder;
import com.example.myapplication.data.ComboItemEntity;
import com.example.myapplication.data.DatabaseProvider;
import com.example.myapplication.data.ProductDao;
import com.example.myapplication.data.ProductEntity;
import com.example.myapplication.data.ProductWithComboItems;
import com.example.myapplication.model.ComboItem;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;
import com.example.myapplication.model.ProductType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 商品目录服务。
 */
public final class StoreCatalogService {

    private final Map<String, String> categoryNames;
    private final ProductDao productDao;

    public StoreCatalogService(Context context) {
        categoryNames = buildCategoryNames();
        productDao = DatabaseProvider.getDatabase(context).productDao();
        CatalogDatabaseSeeder.seedIfNeeded(productDao);
    }

    /**
     * 返回全部分类。
     *
     * @return 分类列表
     */
    public List<ProductCategory> getCategories() {
        return buildCategories();
    }

    /**
     * 按条件查询已上架商品。
     *
     * @param categoryId 分类标识
     * @param keyword 关键字
     * @return 商品列表
     */
    public List<Product> getProducts(String categoryId, String keyword) {
        List<Product> allProducts = mapProducts(productDao.getAllProductsWithComboItems());
        List<Product> results = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.CHINA);
        for (Product product : allProducts) {
            if (!product.isActive()) {
                continue;
            }
            boolean categoryMatched = categoryId == null || categoryId.isEmpty()
                    || categoryId.equals(product.getCategoryId());
            boolean keywordMatched = normalizedKeyword.isEmpty()
                    || product.getName().toLowerCase(Locale.CHINA).contains(normalizedKeyword);
            if (categoryMatched && keywordMatched) {
                results.add(product);
            }
        }
        return results;
    }

    /**
     * 返回全部商品，供管理页展示。
     *
     * @return 全部商品
     */
    public List<Product> getManageProducts() {
        return mapProducts(productDao.getAllProductsWithComboItems());
    }

    /**
     * 返回单个商品。
     *
     * @param productId 商品标识
     * @return 商品信息
     */
    public Product getManageProduct(String productId) {
        ProductWithComboItems productWithComboItems = productDao.getProductWithComboItems(productId);
        return productWithComboItems == null ? null : mapProduct(productWithComboItems);
    }

    /**
     * 返回套餐候选单品。
     *
     * @return 候选单品
     */
    public List<Product> getComboCandidateProducts() {
        List<Product> results = mapProducts(productDao.getNormalProductsWithComboItems());
        results.sort((left, right) -> {
            if (left.isActive() != right.isActive()) {
                return left.isActive() ? -1 : 1;
            }
            return left.getName().compareTo(right.getName());
        });
        return results;
    }

    /**
     * 更新商品上架状态。
     *
     * @param productId 商品标识
     * @param active 是否上架
     */
    public void updateProductActive(String productId, boolean active) {
        ProductWithComboItems productWithComboItems = productDao.getProductWithComboItems(productId);
        if (productWithComboItems == null) {
            return;
        }
        ProductEntity productEntity = productWithComboItems.productEntity;
        productEntity.setActive(active);
        productDao.updateProduct(productEntity);
    }

    /**
     * 新增普通商品。
     *
     * @param categoryId 分类标识
     * @param name 商品名称
     * @param price 商品价格
     */
    public void addNormalProduct(String categoryId, String name, int price) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId("custom_" + System.currentTimeMillis());
        productEntity.setCategoryId(categoryId);
        productEntity.setName(name);
        productEntity.setSpecification("默认规格");
        productEntity.setPrice(price);
        productEntity.setSwatchColorResId(resolveColor(categoryId));
        productEntity.setImageResId(resolveImage(categoryId));
        productEntity.setHotSale(false);
        productEntity.setSalesCount(0);
        productEntity.setProductType(ProductType.NORMAL.name());
        productEntity.setActive(true);
        List<ProductEntity> entities = new ArrayList<>();
        entities.add(productEntity);
        productDao.insertProducts(entities);
    }

    /**
     * 新增套餐商品。
     *
     * @param name 套餐名称
     * @param price 套餐价格
     * @param comboItems 套餐子项
     */
    public void addComboProduct(String name, int price, List<ComboItem> comboItems) {
        String productId = "combo_" + System.currentTimeMillis();
        ProductEntity productEntity = buildComboProductEntity(productId, name, price, true);
        List<ProductEntity> entities = new ArrayList<>();
        entities.add(productEntity);
        productDao.insertProducts(entities);
        productDao.insertComboItems(mapComboItemEntities(productId, comboItems));
    }

    /**
     * 更新套餐商品。
     *
     * @param productId 套餐标识
     * @param name 套餐名称
     * @param price 套餐价格
     * @param comboItems 套餐子项
     */
    public void updateComboProduct(String productId, String name, int price, List<ComboItem> comboItems) {
        ProductWithComboItems productWithComboItems = productDao.getProductWithComboItems(productId);
        if (productWithComboItems == null) {
            return;
        }
        ProductEntity productEntity = productWithComboItems.productEntity;
        productEntity.setName(name);
        productEntity.setPrice(price);
        productDao.updateProduct(productEntity);
        productDao.deleteComboItemsByComboProductId(productId);
        productDao.insertComboItems(mapComboItemEntities(productId, comboItems));
    }

    /**
     * 返回分类名称映射。
     *
     * @return 分类名称映射
     */
    public Map<String, String> getCategoryNameMap() {
        return new LinkedHashMap<>(categoryNames);
    }

    private List<ProductCategory> buildCategories() {
        List<Product> products = mapProducts(productDao.getAllProductsWithComboItems());
        List<ProductCategory> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : categoryNames.entrySet()) {
            int count = 0;
            for (Product product : products) {
                if (product.isActive() && entry.getKey().equals(product.getCategoryId())) {
                    count++;
                }
            }
            result.add(new ProductCategory(
                    entry.getKey(),
                    entry.getValue(),
                    count,
                    "recommend".equals(entry.getKey())
            ));
        }
        return result;
    }

    private List<Product> mapProducts(List<ProductWithComboItems> productWithComboItemsList) {
        List<Product> products = new ArrayList<>();
        for (ProductWithComboItems productWithComboItems : productWithComboItemsList) {
            products.add(mapProduct(productWithComboItems));
        }
        return products;
    }

    private Product mapProduct(ProductWithComboItems productWithComboItems) {
        ProductEntity entity = productWithComboItems.productEntity;
        List<ComboItem> comboItems = new ArrayList<>();
        if (productWithComboItems.comboItemEntities != null) {
            for (ComboItemEntity comboItemEntity : productWithComboItems.comboItemEntities) {
                comboItems.add(new ComboItem(
                        comboItemEntity.getChildProductId(),
                        comboItemEntity.getChildProductNameSnapshot(),
                        comboItemEntity.getQuantity()
                ));
            }
        }
        return new Product(
                entity.getId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getSpecification(),
                entity.getPrice(),
                entity.getSwatchColorResId(),
                entity.getImageResId(),
                entity.isHotSale(),
                entity.getSalesCount(),
                ProductType.valueOf(entity.getProductType()),
                comboItems,
                entity.isActive()
        );
    }

    private ProductEntity buildComboProductEntity(String productId, String name, int price, boolean active) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setCategoryId("combo");
        productEntity.setName(name);
        productEntity.setSpecification("套餐商品");
        productEntity.setPrice(price);
        productEntity.setSwatchColorResId(R.color.product_image_green);
        productEntity.setImageResId(R.drawable.drink_mango);
        productEntity.setHotSale(false);
        productEntity.setSalesCount(0);
        productEntity.setProductType(ProductType.COMBO.name());
        productEntity.setActive(active);
        return productEntity;
    }

    private List<ComboItemEntity> mapComboItemEntities(String comboProductId, List<ComboItem> comboItems) {
        List<ComboItemEntity> entities = new ArrayList<>();
        for (ComboItem comboItem : comboItems) {
            ComboItemEntity entity = new ComboItemEntity();
            entity.setComboProductId(comboProductId);
            entity.setChildProductId(comboItem.getChildProductId());
            entity.setChildProductNameSnapshot(comboItem.getChildProductNameSnapshot());
            entity.setQuantity(comboItem.getQuantity());
            entities.add(entity);
        }
        return entities;
    }

    private Map<String, String> buildCategoryNames() {
        Map<String, String> names = new LinkedHashMap<>();
        names.put("recommend", "经典推荐");
        names.put("milk", "鲜奶茶");
        names.put("fruit", "水果茶");
        names.put("cheese", "芝士奶盖");
        names.put("herb", "奶昔系列");
        names.put("tea", "纯茶");
        names.put("topping", "小料/配料");
        names.put("sweet", "甜度冰量");
        names.put("combo", "套餐组合");
        return names;
    }

    private int resolveColor(String categoryId) {
        if ("fruit".equals(categoryId)) {
            return R.color.product_image_fruit;
        }
        if ("tea".equals(categoryId) || "topping".equals(categoryId)) {
            return R.color.product_image_tea;
        }
        if ("cheese".equals(categoryId) || "recommend".equals(categoryId)) {
            return R.color.product_image_milk;
        }
        return R.color.product_image_green;
    }

    private int resolveImage(String categoryId) {
        if ("fruit".equals(categoryId)) {
            return R.drawable.drink_strawberry;
        }
        if ("tea".equals(categoryId)) {
            return R.drawable.drink_lemon_tea;
        }
        if ("combo".equals(categoryId)) {
            return R.drawable.drink_mango;
        }
        return R.drawable.drink_pearl_milk_tea;
    }
}
