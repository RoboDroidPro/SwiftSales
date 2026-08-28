package com.example.salestracker

import com.example.salestracker.data.model.SaleEventWithItems
import com.example.salestracker.data.model.SaleItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepository @Inject constructor(private val dao: SaleDao) {

    val allSales: Flow<List<SaleEventWithItems>> = dao.getAllSalesWithProducts()

    suspend fun getSaleById(id: String) : SaleEventWithItems? =
        dao.getSaleWithItems(id)

    suspend fun upsertSale(saleEvent: SaleEvent) {
        dao.upsertSale(saleEvent)
    }

    suspend fun upsertSaleWithItems(event: SaleEvent, items: List<SaleItem>) {
        dao.upsertSale(event)
        dao.insertSaleItems(items)
    }

    suspend fun insertSaleWithItems(event: SaleEvent, items: List<SaleItem>) {
        dao.upsertSale(event)
        dao.insertSaleItems(items)
    }

    suspend fun deleteSales(saleEvents: List<SaleEvent>) {
        dao.deleteSales(saleEvents)
    }
}
