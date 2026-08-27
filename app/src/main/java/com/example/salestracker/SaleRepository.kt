package com.example.salestracker

import com.example.salestracker.data.model.SaleWithItems
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepository @Inject constructor(private val dao: SaleDao) {

    val allSales: Flow<List<SaleWithItems>> = dao.getAllSalesWithProducts()

    suspend fun getSaleById(id: String) : SaleWithItems? =
        dao.getSaleWithItems(id)

    suspend fun upsertSale(sale: Sale) {
        dao.upsertSale(sale)
    }

    suspend fun upsertSaleWithItems(saleWithItems: SaleWithItems) {
        dao.upsertSale(saleWithItems.sale)
    }

    suspend fun deleteSales(sales: List<com.example.salestracker.Sale>) {
        dao.deleteSales(sales)
    }
}
