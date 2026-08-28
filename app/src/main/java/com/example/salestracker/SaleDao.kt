package com.example.salestracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.salestracker.data.model.SaleEventWithItems
import com.example.salestracker.data.model.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Transaction
    suspend fun upsertSaleWithItems(event: SaleEvent, items: List<SaleItem>) {
        upsertSale(event)
        insertSaleItems(items)
    }

    // Inserts a sale into the database
    @Upsert
    suspend fun upsertSale(saleEvent: SaleEvent)

    @Insert
    suspend fun insertSaleItems(items: List<SaleItem>)

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<SaleEvent>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSale(id: String) : SaleEvent

    @Delete
    suspend fun deleteSales(saleEvents: List<SaleEvent>)

    //----------------
    @Transaction
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSalesWithProducts(): Flow<List<SaleEventWithItems>>

    @Transaction
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleWithItems(id: String): SaleEventWithItems?
    //----------------
}