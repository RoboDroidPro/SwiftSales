package com.example.salestracker.hilt

import android.content.Context
import androidx.room.Room
import com.example.salestracker.data.database.MIGRATION_1_2
import com.example.salestracker.data.database.SaleDatabase
import com.example.salestracker.data.database.ProductDao
import com.example.salestracker.data.database.SaleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DIModules {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SaleDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SaleDatabase::class.java,
            "sale_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideSaleDao(db: SaleDatabase): SaleDao = db.saleDao()

    @Provides
    fun provideProductDao(db: SaleDatabase) : ProductDao = db.productDao()
}