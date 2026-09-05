package com.example.salestracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.salestracker.data.model.ProductSale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    // Inserts a sale into the database
    @Upsert
    suspend fun upsertSale(sale: Sale)

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSale(id: String) : Sale

    @Delete
    suspend fun deleteSales(sales: List<Sale>)

    //----------------
    @Transaction
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSalesWithProducts(): Flow<List<ProductSale>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getProductSale(id: String): ProductSale?
    //----------------
}