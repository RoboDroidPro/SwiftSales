package com.example.salestracker

import com.example.salestracker.data.model.SaleItem
import com.example.salestracker.data.model.SaleWithItems
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepository @Inject constructor(private val dao: SaleDao) {

    val allSales: Flow<List<SaleWithItems>> = dao.getAllSalesWithProducts()

    suspend fun getSaleById(id: String) : SaleWithItems? =
        dao.getSaleWithItems(id)

    suspend fun upsertSale(saleEvent: SaleEvent) {
        dao.upsertSale(saleEvent)
    }

    suspend fun upsertSaleWithItems(event: SaleEvent, items: List<SaleItem>) {
        dao.upsertSale(event)
    }

    suspend fun insertSaleWithItems(saleWithItems: SaleWithItems) {
        dao.upsertSale(saleWithItems.saleEvent)
    }

    suspend fun deleteSales(saleEvents: List<SaleEvent>) {
        dao.deleteSales(saleEvents)
    }
}
