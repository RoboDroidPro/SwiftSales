package com.example.salestracker.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.salestracker.data.model.ProductItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY orderIndex")
    fun getAllProducts(): Flow<List<ProductItem>>

    @Query("SELECT * FROM products ORDER BY orderIndex")
    suspend fun getProductsList(): List<ProductItem>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Upsert
    suspend fun upsertProduct(productItem: ProductItem)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)
}