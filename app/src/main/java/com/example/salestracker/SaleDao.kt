package com.example.salestracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.salestracker.data.model.SaleWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    // Inserts a sale into the database
    @Upsert
    suspend fun upsertSale(sale: com.example.salestracker.Sale)

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<com.example.salestracker.Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSale(id: String) : com.example.salestracker.Sale

    @Delete
    suspend fun deleteSales(sales: List<com.example.salestracker.Sale>)

    //----------------
    @Transaction
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSalesWithProducts(): Flow<List<SaleWithItems>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithItems(id: String): SaleWithItems?
    //----------------
}