package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.data.model.Product

sealed interface AddSaleAction {
    data class DateChanged(val newDate: String) : AddSaleAction
    data class BuyerChanged(val newBuyer: String) : AddSaleAction
    data class TotalSalePriceChanged(val newSalePrice: String) : AddSaleAction
    data class SaleNotesChanged(val newNotes: String) : AddSaleAction

    object AddSaleItem : AddSaleAction
    data class RemoveSaleItem(val itemId: String) : AddSaleAction

    data class SaleEntryAction(val itemId: String, val action: SaleItemAction) : AddSaleAction

    object SaveSale : AddSaleAction
}

sealed interface SaleItemAction {
    data class ProductChanged(val newProduct: Product) : SaleItemAction
    data class ProductPriceChanged(val newPrice: String) : SaleItemAction
    data class QuantityChanged(val newQuantity: String) : SaleItemAction
}