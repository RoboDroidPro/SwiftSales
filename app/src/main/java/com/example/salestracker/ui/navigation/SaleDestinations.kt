package com.example.salestracker.ui.navigation

import androidx.navigation.NavHostController
import com.example.salestracker.ui.navigation.SaleDestinationsArgs.SALE_ID_ARG
import com.example.salestracker.ui.navigation.SaleDestinationsArgs.TITLE_ARG

object SaleDestinationsArgs {
    const val TITLE_ARG = "title"
    const val USER_MESSAGE_ARG = "userMessage"
    const val SALE_ID_ARG = "saleId"
}

object SaleDestinations {
    const val SALES_LIST_ROUTE = "sales_list"
    const val ADD_EDIT_SALE_ROUTE = "add_edit_sale"
    const val SETTINGS_ROUTE = "settings"

    const val ADD_EDIT_SALE_ROUTE_WITH_ARGS =
        "$ADD_EDIT_SALE_ROUTE?$TITLE_ARG={$TITLE_ARG}&$SALE_ID_ARG={$SALE_ID_ARG}"

    fun addEditSaleRoute(title: String, saleId: String?) : String {
        return buildString {
            append("$ADD_EDIT_SALE_ROUTE?$TITLE_ARG=$title")
            if (saleId != null) append("&$SALE_ID_ARG=$saleId")
        }
    }
}

class SaleNavigationActions(private val navController: NavHostController) {

    fun navigateAddEditSale(title:String, saleId: String?) {
        val route = SaleDestinations.addEditSaleRoute(title, saleId)
        navController.navigate(route)
    }

    fun navigateToAllSales() {
        navController.navigate(SaleDestinations.SALES_LIST_ROUTE)
    }

    fun navigateToSettings() {
        navController.navigate(SaleDestinations.SETTINGS_ROUTE)
    }
}

object SalesListDes

data class AddEditSaleDes(val id: String? = null)

object SettingsDes

