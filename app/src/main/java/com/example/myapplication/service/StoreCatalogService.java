package com.example.myapplication.service;

import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 商品目录服务。
 */
public final class StoreCatalogService {

    private final List<ProductCategory> categories;
    private final List<Product> products;

    public StoreCatalogService() {
        products = Arrays.asList(
                new Product("p01", "recommend", "珍珠奶茶", "大杯/正常糖/少冰", 14, R.color.product_image_milk, R.drawable.drink_pearl_milk_tea, true, 328),
                new Product("p02", "recommend", "杨枝甘露", "大杯/少糖/少冰", 20, R.color.product_image_fruit, R.drawable.drink_mango, true, 287),
                new Product("p03", "recommend", "黑糖珍珠鲜奶", "大杯/正常糖/少冰", 18, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, true, 245),
                new Product("p04", "recommend", "阳光青提", "大杯/三分糖/少冰", 22, R.color.product_image_green, R.drawable.drink_citrus_tea, true, 198),
                new Product("p05", "recommend", "草莓啵啵", "大杯/正常糖/去冰", 19, R.color.product_image_fruit, R.drawable.drink_strawberry, true, 176),
                new Product("p06", "recommend", "桃气乌龙", "大杯/少糖/少冰", 18, R.color.product_image_fruit, R.drawable.drink_lemon_tea, true, 152),
                new Product("p07", "recommend", "芝士茉莉绿茶", "大杯/正常糖/去冰", 16, R.color.product_image_milk, R.drawable.drink_matcha, false, 0),
                new Product("p08", "recommend", "满杯芒果", "大杯/少糖/少冰", 23, R.color.product_image_fruit, R.drawable.drink_mango, false, 0),
                new Product("p09", "recommend", "柠檬红茶", "大杯/三分糖/去冰", 12, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0),
                new Product("p10", "recommend", "霸气柠檬", "大杯/正常糖/少冰", 16, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0),
                new Product("p11", "recommend", "红豆布丁奶茶", "大杯/正常糖/热饮", 17, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0),
                new Product("p12", "recommend", "香芋奶昔", "大杯/正常糖/少冰", 19, R.color.product_image_milk, R.drawable.drink_strawberry, false, 0),

                new Product("p13", "milk", "伯牙绝弦", "大杯/正常糖/热饮", 17, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0),
                new Product("p14", "milk", "招牌冻顶奶茶", "大杯/正常糖/少冰", 15, R.color.product_image_milk, R.drawable.drink_pearl_milk_tea, false, 0),
                new Product("p15", "milk", "茉香奶绿", "大杯/三分糖/少冰", 14, R.color.product_image_green, R.drawable.drink_matcha, false, 0),
                new Product("p16", "milk", "琥珀奶茶", "大杯/正常糖/热饮", 16, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, false, 0),

                new Product("p17", "fruit", "西柚多多", "大杯/少糖/少冰", 18, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0),
                new Product("p18", "fruit", "葡萄冰萃", "大杯/少糖/少冰", 21, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0),
                new Product("p19", "fruit", "多肉桃桃", "大杯/少糖/少冰", 19, R.color.product_image_fruit, R.drawable.drink_lemon_tea, false, 0),
                new Product("p20", "fruit", "鲜橙茉莉", "大杯/正常糖/少冰", 17, R.color.product_image_fruit, R.drawable.drink_mango, false, 0),

                new Product("p21", "cheese", "芝士四季春", "大杯/正常糖/去冰", 18, R.color.product_image_milk, R.drawable.drink_citrus_tea, false, 0),
                new Product("p22", "cheese", "芝士青提", "大杯/少糖/少冰", 22, R.color.product_image_green, R.drawable.drink_matcha, false, 0),
                new Product("p23", "cheese", "芝芝莓莓", "大杯/正常糖/去冰", 21, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0),
                new Product("p24", "cheese", "芝士芒芒", "大杯/少糖/少冰", 22, R.color.product_image_fruit, R.drawable.drink_mango, false, 0),

                new Product("p25", "herb", "香草奶昔", "大杯/正常糖/少冰", 18, R.color.product_image_milk, R.drawable.drink_red_bean_milk_tea, false, 0),
                new Product("p26", "herb", "奥利奥奶昔", "大杯/正常糖/少冰", 20, R.color.product_image_tea, R.drawable.drink_brown_sugar_milk_tea, false, 0),
                new Product("p27", "herb", "草莓奶昔", "大杯/正常糖/去冰", 19, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0),
                new Product("p28", "herb", "芒果奶昔", "大杯/少糖/少冰", 21, R.color.product_image_fruit, R.drawable.drink_mango, false, 0),

                new Product("p29", "tea", "金凤红茶", "大杯/三分糖/热饮", 10, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0),
                new Product("p30", "tea", "高山乌龙", "大杯/无糖/热饮", 11, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0),
                new Product("p31", "tea", "碧玉茉莉", "大杯/无糖/少冰", 12, R.color.product_image_green, R.drawable.drink_matcha, false, 0),
                new Product("p32", "tea", "手作绿茶", "大杯/无糖/少冰", 10, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0),

                new Product("p33", "topping", "珍珠加料", "标准份", 3, R.color.product_image_tea, R.drawable.drink_pearl_milk_tea, false, 0),
                new Product("p34", "topping", "椰果加料", "标准份", 3, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0),
                new Product("p35", "topping", "布丁加料", "标准份", 4, R.color.product_image_milk, R.drawable.drink_mango, false, 0),
                new Product("p36", "topping", "红豆加料", "标准份", 4, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0),

                new Product("p37", "sweet", "正常糖正常冰", "基础规格", 0, R.color.product_image_milk, R.drawable.drink_matcha, false, 0),
                new Product("p38", "sweet", "少糖少冰", "基础规格", 0, R.color.product_image_green, R.drawable.drink_citrus_tea, false, 0),
                new Product("p39", "sweet", "三分糖去冰", "基础规格", 0, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0),
                new Product("p40", "sweet", "无糖热饮", "基础规格", 0, R.color.product_image_tea, R.drawable.drink_lemon_tea, false, 0),

                new Product("p41", "combo", "双杯下午茶套餐", "两杯组合/少糖/少冰", 29, R.color.product_image_green, R.drawable.drink_mango, false, 0),
                new Product("p42", "combo", "奶茶加小料套餐", "一杯一料", 19, R.color.product_image_milk, R.drawable.drink_pearl_milk_tea, false, 0),
                new Product("p43", "combo", "果茶双拼套餐", "双杯组合", 33, R.color.product_image_fruit, R.drawable.drink_strawberry, false, 0),
                new Product("p44", "combo", "热饮轻享套餐", "双杯组合", 24, R.color.product_image_tea, R.drawable.drink_red_bean_milk_tea, false, 0)
        );
        categories = buildCategories();
    }

    /**
     * 返回全部分类。
     *
     * @return 分类列表
     */
    public List<ProductCategory> getCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * 按条件查询商品。
     *
     * @param categoryId 分类标识
     * @param keyword 关键字
     * @return 商品列表
     */
    public List<Product> getProducts(String categoryId, String keyword) {
        List<Product> results = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.CHINA);
        for (Product product : products) {
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

    private List<ProductCategory> buildCategories() {
        Map<String, String> categoryNames = new LinkedHashMap<>();
        categoryNames.put("recommend", "经典推荐");
        categoryNames.put("milk", "鲜奶茶");
        categoryNames.put("fruit", "水果茶");
        categoryNames.put("cheese", "芝士奶盖");
        categoryNames.put("herb", "奶昔系列");
        categoryNames.put("tea", "纯茶");
        categoryNames.put("topping", "小料/配料");
        categoryNames.put("sweet", "甜度冰量");
        categoryNames.put("combo", "套餐组合");

        List<ProductCategory> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : categoryNames.entrySet()) {
            int count = 0;
            for (Product product : products) {
                if (entry.getKey().equals(product.getCategoryId())) {
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
}
