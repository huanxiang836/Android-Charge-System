package com.example.myapplication.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * 商品及套餐子项关联模型。
 */
public final class ProductWithComboItems {

    @Embedded
    public ProductEntity productEntity;

    @Relation(parentColumn = "id", entityColumn = "comboProductId")
    public List<ComboItemEntity> comboItemEntities;
}
