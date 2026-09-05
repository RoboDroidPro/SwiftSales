package com.example.salestracker.data.repository

import com.example.salestracker.data.database.ProductDao
import com.example.salestracker.data.model.ProductItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productDao: ProductDao) {

    val productsList: Flow<List<ProductItem>> = productDao.getAllProducts()

    //UPSERT
    suspend fun upsertProductItem(productItem: ProductItem) {
        productDao.upsertProduct(productItem)
    }

    //DELETE ALL
    suspend fun deleteProductItems() {
        productDao.deleteAllProducts()
    }

    //DELETE
    suspend fun deleteProduct(productId: String) {
        productDao.deleteProduct(productId)
    }
}