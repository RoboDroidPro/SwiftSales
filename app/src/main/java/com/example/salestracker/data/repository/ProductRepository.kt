package com.example.salestracker.data.repository

import com.example.salestracker.data.database.ProductDao
import com.example.salestracker.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productDao: ProductDao) {

    val productsList: Flow<List<Product>> = productDao.getAllProducts()

    //UPSERT
    suspend fun upsertProductItem(product: Product) {
        productDao.upsertProduct(product)
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