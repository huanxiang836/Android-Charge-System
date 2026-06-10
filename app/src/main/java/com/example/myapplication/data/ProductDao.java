package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

/**
 * 商品数据访问对象。
 */
@Dao
public interface ProductDao {

    /**
     * 返回商品总数。
     *
     * @return 商品总数
     */
    @Query("SELECT COUNT(*) FROM products")
    int countProducts();

    /**
     * 插入商品。
     *
     * @param productEntities 商品列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<ProductEntity> productEntities);

    /**
     * 插入套餐子项。
     *
     * @param comboItemEntities 套餐子项列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComboItems(List<ComboItemEntity> comboItemEntities);

    /**
     * 查询全部商品。
     *
     * @return 全部商品
     */
    @Transaction
    @Query("SELECT * FROM products")
    List<ProductWithComboItems> getAllProductsWithComboItems();

    /**
     * 查询单个商品。
     *
     * @param productId 商品标识
     * @return 商品信息
     */
    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    ProductWithComboItems getProductWithComboItems(String productId);

    /**
     * 查询全部普通商品。
     *
     * @return 普通商品
     */
    @Transaction
    @Query("SELECT * FROM products WHERE productType = 'NORMAL'")
    List<ProductWithComboItems> getNormalProductsWithComboItems();

    /**
     * 更新商品。
     *
     * @param productEntity 商品实体
     */
    @Update
    void updateProduct(ProductEntity productEntity);

    /**
     * 删除套餐子项。
     *
     * @param comboProductId 套餐标识
     */
    @Query("DELETE FROM combo_items WHERE comboProductId = :comboProductId")
    void deleteComboItemsByComboProductId(String comboProductId);
}
