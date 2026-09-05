package com.example.salestracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.salestracker.data.database.ProductDao
import com.example.salestracker.data.model.ProductItem

@Database(
    entities = [Sale::class, ProductItem::class],
    version = 2,
    exportSchema = false
)
abstract class SaleDatabase : RoomDatabase() {
    abstract fun saleDao(): SaleDao
    abstract fun productDao(): ProductDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE products ADD COLUMN inStock INTEGER NOT NULL DEFAULT 1"
        )
    }
}

