package com.example.salestracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.salestracker.data.model.Product
import com.example.salestracker.data.model.SaleEvent
import com.example.salestracker.data.model.SaleItem

@Database(
    entities = [
        SaleEvent::class,
        SaleItem::class,
        Product::class
    ],
    version = 3,
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

