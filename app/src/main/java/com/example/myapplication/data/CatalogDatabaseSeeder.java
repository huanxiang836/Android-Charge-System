package com.example.myapplication.data;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品目录种子数据初始化器。
 */
public final class CatalogDatabaseSeeder {

    private CatalogDatabaseSeeder() {
    }

    /**
     * 在数据库为空时写入初始商品与套餐。
     *
     * @param productDao 商品 DAO
     */
    public static void seedIfNeeded(ProductDao productDao) {
        if (productDao.countProducts() > 0) {
            return;
        }

        List<ProductEntity> productEntities = new ArrayList<>();
        productEntities.add(createNormal("p01", "recommend", "珍珠奶茶", 14, R.color.product_image_milk, R.drawable.drink_pearl_milk_tea, true, 328));
        productEntities.add(createNormal("p02", "recommend", "杨枝甘露", 20, R.color.product_image_fruit, R.drawable.drink_mango, true, 287));
        productEntities.add(createNormal("p03", "recommend", "黑糖珍珠鲜奶", 18, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, true, 245));
        productEntities.add(createNormal("p04", "recommend", "阳光青提", 22, R.color.product_image_green, R.drawable.drink_citrus_tea, true, 198));
        productEntities.add(createNormal("p05", "recommend", "草莓啵啵", 19, R.color.product_image_fruit, R.drawable.drink_strawberry, true, 176));
        productEntities.add(createNormal("p06", "recommend", "桃气乌龙", 18, R.color.product_image_fruit, R.drawable.drink_lemon_tea, true, 152));
        productEntities.add(createNormal("p07", "recommend", "芝士茉莉绿茶", 16, R.color.product_image_milk, R.drawable.drink_matcha, false, 0));
        productEntities.add(createNormal("p08", "recommend", "满杯芒果", 23, R.color.product_image_fruit, R.drawable.drink_mango, false, 0));
        productEntities.add(createNormal("p09", "recommend", "柠檬红茶", 12, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0));
        productEntities.add(createNormal("p10", "recommend", "霸气柠檬", 16, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0));
        productEntities.add(createNormal("p11", "recommend", "红豆布丁奶茶", 17, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0));
        productEntities.add(createNormal("p12", "recommend", "香芋奶昔", 19, R.color.product_image_milk, R.drawable.drink_strawberry, false, 0));
        productEntities.add(createNormal("p13", "milk", "伯牙绝弦", 17, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0));
        productEntities.add(createNormal("p14", "milk", "招牌冻顶奶茶", 15, R.color.product_image_milk, R.drawable.drink_pearl_milk_tea, false, 0));
        productEntities.add(createNormal("p15", "milk", "茉香奶绿", 14, R.color.product_image_green, R.drawable.drink_matcha, false, 0));
        productEntities.add(createNormal("p16", "milk", "琥珀奶茶", 16, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, false, 0));
        productEntities.add(createNormal("p17", "fruit", "西柚多多", 18, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0));
        productEntities.add(createNormal("p18", "fruit", "葡萄冰萃", 21, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0));
        productEntities.add(createNormal("p19", "fruit", "多肉桃桃", 19, R.color.product_image_fruit, R.drawable.drink_lemon_tea, false, 0));
        productEntities.add(createNormal("p20", "fruit", "鲜橙茉莉", 17, R.color.product_image_fruit, R.drawable.drink_mango, false, 0));
        productEntities.add(createNormal("p21", "cheese", "芝士四季春", 18, R.color.product_image_milk, R.drawable.drink_citrus_tea, false, 0));
        productEntities.add(createNormal("p22", "cheese", "芝士青提", 22, R.color.product_image_green, R.drawable.drink_matcha, false, 0));
        productEntities.add(createNormal("p23", "cheese", "芝芝莓莓", 21, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0));
        productEntities.add(createNormal("p24", "cheese", "芝士芒芒", 22, R.color.product_image_fruit, R.drawable.drink_mango, false, 0));
        productEntities.add(createNormal("p25", "herb", "香草奶昔", 18, R.color.product_image_milk, R.drawable.drink_red_bean_milk_tea, false, 0));
        productEntities.add(createNormal("p26", "herb", "奥利奥奶昔", 20, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, false, 0));
        productEntities.add(createNormal("p27", "herb", "草莓奶昔", 19, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0));
        productEntities.add(createNormal("p28", "herb", "芒果奶昔", 21, R.color.product_image_fruit, R.drawable.drink_mango, false, 0));
        productEntities.add(createNormal("p29", "tea", "金凤红茶", 10, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0));
        productEntities.add(createNormal("p30", "tea", "高山乌龙", 11, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0));
        productEntities.add(createNormal("p31", "tea", "碧玉茉莉", 12, R.color.product_image_green, R.drawable.drink_matcha, false, 0));
        productEntities.add(createNormal("p32", "tea", "手作绿茶", 10, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0));
        productEntities.add(createCombo("p33", "双杯下午茶套餐", 29));
        productEntities.add(createCombo("p34", "奶茶加小料套餐", 19));
        productEntities.add(createCombo("p35", "果茶双拼套餐", 33));
        productEntities.add(createCombo("p36", "热饮轻享套餐", 24));
        productDao.insertProducts(productEntities);

        List<ComboItemEntity> comboItemEntities = new ArrayList<>();
        comboItemEntities.add(createComboItem("p33", "p01", "珍珠奶茶", 1));
        comboItemEntities.add(createComboItem("p33", "p09", "柠檬红茶", 1));
        comboItemEntities.add(createComboItem("p34", "p14", "招牌冻顶奶茶", 1));
        comboItemEntities.add(createComboItem("p34", "p29", "金凤红茶", 1));
        comboItemEntities.add(createComboItem("p35", "p17", "西柚多多", 1));
        comboItemEntities.add(createComboItem("p35", "p19", "多肉桃桃", 1));
        comboItemEntities.add(createComboItem("p36", "p13", "伯牙绝弦", 1));
        comboItemEntities.add(createComboItem("p36", "p29", "金凤红茶", 1));
        productDao.insertComboItems(comboItemEntities);
    }

    private static ProductEntity createNormal(
            String id,
            String categoryId,
            String name,
            int price,
            int swatchColorResId,
            int imageResId,
            boolean hotSale,
            int salesCount
    ) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setCategoryId(categoryId);
        entity.setName(name);
        entity.setSpecification("默认规格");
        entity.setPrice(price);
        entity.setSwatchColorResId(swatchColorResId);
        entity.setImageResId(imageResId);
        entity.setHotSale(hotSale);
        entity.setSalesCount(salesCount);
        entity.setProductType("NORMAL");
        entity.setActive(true);
        return entity;
    }

    private static ProductEntity createCombo(String id, String name, int price) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setCategoryId("combo");
        entity.setName(name);
        entity.setSpecification("套餐商品");
        entity.setPrice(price);
        entity.setSwatchColorResId(R.color.product_image_green);
        entity.setImageResId(R.drawable.drink_mango);
        entity.setHotSale(false);
        entity.setSalesCount(0);
        entity.setProductType("COMBO");
        entity.setActive(true);
        return entity;
    }

    private static ComboItemEntity createComboItem(String comboId, String childId, String childName, int quantity) {
        ComboItemEntity entity = new ComboItemEntity();
        entity.setComboProductId(comboId);
        entity.setChildProductId(childId);
        entity.setChildProductNameSnapshot(childName);
        entity.setQuantity(quantity);
        return entity;
    }
}
