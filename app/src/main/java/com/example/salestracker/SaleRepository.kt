package com.example.salestracker

import com.example.salestracker.data.model.ProductSale
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepository @Inject constructor(private val dao: SaleDao) {

    val allProductSales: Flow<List<ProductSale>> = dao.getAllSalesWithProducts()

    suspend fun getSaleById(id: String) : ProductSale? =
        dao.getProductSale(id)

    suspend fun upsertSale(sale: Sale) {
        dao.upsertSale(sale)
    }

    suspend fun deleteSales(sales: List<Sale>) {
        dao.deleteSales(sales)
    }
}
