package com.example.myapplication.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * 应用数据库。
 */
@Database(
        entities = {
                ProductEntity.class,
                ComboItemEntity.class,
                MemberEntity.class,
                MemberConfigEntity.class,
                MemberOrderStatEntity.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 返回商品 DAO。
     *
     * @return 商品 DAO
     */
    public abstract ProductDao productDao();

    /**
     * 返回会员 DAO。
     *
     * @return 会员 DAO
     */
    public abstract MemberDao memberDao();

    /**
     * 返回会员配置 DAO。
     *
     * @return 会员配置 DAO
     */
    public abstract MemberConfigDao memberConfigDao();

    /**
     * 返回会员订单统计 DAO。
     *
     * @return 会员订单统计 DAO
     */
    public abstract MemberOrderStatDao memberOrderStatDao();
}
