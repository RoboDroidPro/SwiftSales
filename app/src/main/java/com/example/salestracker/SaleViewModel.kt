package com.example.salestracker

/*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(private val repository: SaleRepository) : ViewModel() {
   // val product = mutableStateOf("")
    val buyer = mutableStateOf("")
    val selectedProduct = mutableStateOf(Product.None)
    val quantity = mutableIntStateOf(0)
    val totalSalePrice = mutableStateOf("")
    val notes = mutableStateOf("")
    val date = mutableStateOf(getTodayDate())

    val allSales = repository.allSales

    val selectedSales = mutableStateListOf<Sale>()

    val confirmingDeletion = mutableStateOf(false)

    fun saveSale() {
         viewModelScope.launch {
            val sale = Sale(
                id = UUID.randomUUID().toString(),
                date = date.value,
                product = selectedProduct.value.displayName, //stored as a string
                buyer = buyer.value,
                quantity = quantity.intValue,
                totalSalePrice = totalSalePrice.value.toDoubleOrNull() ?: 0.0,
                saleNotes = notes.value.takeIf { it.isNotBlank() }
            )
            repository.upsertSale(sale)
            clearFields()
        }
    }

    private fun clearFields() {
        buyer.value = ""
        selectedProduct.value = Product.None
        quantity.intValue = 0
        totalSalePrice.value = ""
        notes.value = ""
    }

    private fun getTodayDate(): String {
        return LocalDate.now().toString()
    }

    fun formatDateForDisplay(dateString: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy") // e.g., 5-July-2025
        return try {
            val date = LocalDate.parse(dateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString // fallback in case of parsing error
        }
    }


    //All Sales Screen functions

    fun toggleSelection(sale: Sale) {
        if (selectedSales.contains(sale)) {
            selectedSales.remove(sale)
        } else {
            selectedSales.add(sale)
        }
    }

    fun deleteSelectedSales() {
        viewModelScope.launch {
            repository.deleteSales(selectedSales)
            selectedSales.clear()
            confirmingDeletion.value = false
        }
    }

    fun cancelDeletionConfirmation() {
        confirmingDeletion.value = false
    }


    fun clearSelection() {
        selectedSales.clear()
        confirmingDeletion.value = false
    }

    fun selectAll(sales: List<Sale>) {
        selectedSales.clear()
        selectedSales.addAll(sales)
    }

}*/
