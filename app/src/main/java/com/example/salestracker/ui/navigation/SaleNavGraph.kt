package com.example.salestracker.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.salestracker.R
import com.example.salestracker.ui.components.SalesNavDrawer
import com.example.salestracker.ui.screens.AddEditSaleScreen
import com.example.salestracker.ui.screens.AllSalesScreen
import com.example.salestracker.ui.screens.settings.SettingsScreen
import com.example.salestracker.viewModel.AllSalesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "ArduinoSNGraph"
const val SNACKBAR_MSG_KEY = "snackbar_msg"

@Composable
fun SaleNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = SaleDestinations.SALES_LIST_ROUTE, //sd.SLR is "sales_list"
    navActions: SaleNavigationActions = remember(navController) {
        SaleNavigationActions(navController)
    }
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        /** Step #5.
         * SALES_LIST_ROUTE – AllSalesScreen (the parent destination)
         *
         * This composable is the heart of the snackbar journey when returning from AddEditSaleScreen.
         * Its job is to:
         * 1. Provide a stable ViewModel scoped to the SALES_LIST_ROUTE (so it survives navigation).
         * 2. Give AllSalesScreen direct access to the SavedStateHandle that belongs to this exact back-stack entry.
         *
         * Why do we need this SavedStateHandle here?
         *
         * When the user saves a sale (add or edit) in AddEditSaleScreen:
         *   • AddEditSaleScreen writes the string resource ID of the success message
         *     (e.g. R.string.save_success) into the previous back-stack entry’s SavedStateHandle
         *     using the key SNACKBAR_MSG_KEY.
         *   • It then calls navController.popBackStack().
         *
         * The previous back-stack entry is exactly this SALES_LIST_ROUTE entry.
         * Therefore, when AllSalesScreen recomposes after the pop, it can read that
         * SavedStateHandle and immediately know “hey, a sale was just saved – show a snackbar”.
         *
         * Key lines explained:
         *
         *   val parentEntry = remember(backStackEntry) {
         *       navController.getBackStackEntry(SaleDestinations.SALES_LIST_ROUTE)
         *   }
         *   → Guarantees we always get the real back-stack entry for the list screen,
         *      even when we are inside a nested NavHost or after configuration changes.
         *
         *   val allSalesViewModel = hiltViewModel<AllSalesViewModel>(parentEntry)
         *   → Scopes the ViewModel to the list destination so it survives the AddEdit → List round-trip.
         *
         *   val parentSavedStateHandle = parentEntry.savedStateHandle
         *   → This is the exact SavedStateHandle that AddEditSaleScreen wrote to a moment ago.
         *
         *   savedStateHandle = parentSavedStateHandle   (passed into AllSalesScreen)
         *   → AllSalesScreen uses this handle inside a LaunchedEffect to observe
         *      savedStateHandle.getStateFlow<Int?>(SNACKBAR_MSG_KEY, null).
         *      As soon as the flow emits the resource ID, the snackbar is shown and the key is cleared
         *      (set to null) so the message never appears again on future recompositions or back presses.
         *
         * Result: One-time, reliable, configuration-change-safe snackbar that only appears
         * when a real save happened – no SharedFlow replay issues, no extra ViewModel events.
         * This is the pattern recommended by Google for returning simple results between
         * Compose destinations. Next step is in [AllSalesScreen]
         */
        //Sales List
        composable(
            route = SaleDestinations.SALES_LIST_ROUTE
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(SaleDestinations.SALES_LIST_ROUTE)
            }
            // we need an instance of the AllSalesViewModel — get it from hiltViewModel()
            val allSalesViewModel = hiltViewModel<AllSalesViewModel>(parentEntry)

            val parentSavedStateHandle = parentEntry.savedStateHandle

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { entry ->
                    drawerState.close()
                }
            }

            SalesNavDrawer(
                onNavToSettings = {
                    navActions.navigateToSettings()
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                drawerState = drawerState
            ) {
                AllSalesScreen(
                    onAddEditSale = { productSale ->
                        navActions.navigateAddEditSale(
                            title = "Add Sale",
                            saleId = productSale?.saleEvent?.id
                        )
                    },
                    onMenuClick = { coroutineScope.launch { drawerState.open() } },
                    viewModel = allSalesViewModel,
                    savedStateHandle = parentSavedStateHandle  // new param
                )
            }
        }
        //AddEditSale Screen
        composable(
            route = SaleDestinations.ADD_EDIT_SALE_ROUTE_WITH_ARGS,
//            "$ADD_EDIT_SALE_ROUTE?$TITLE_ARG={$TITLE_ARG}&$SALE_ID_ARG={$SALE_ID_ARG}"
            arguments = listOf(
                navArgument(SaleDestinationsArgs.TITLE_ARG) { //title
                    type = NavType.StringType
                    defaultValue = "Add Sale"
                },
                navArgument(SaleDestinationsArgs.SALE_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            AddEditSaleScreen(
                onNavigateToAllSales = { resultCode ->
                    /**
                     * Step #4. Takes the result String passed to onNavigateSales,
                     * and puts it into the saved state handle of the previous backStackEntry
                     * the savedStateHandle now contains the result code. Next step is in the AllSales
                     * composable{} block
                     */
                    val previousHandle = navController.previousBackStackEntry?.savedStateHandle
                    val msgRes = when (resultCode) {
                        ADD_RESULT_OK -> R.string.save_success
                        EDIT_RESULT_OK -> R.string.edit_success
                        else -> null
                    }
                    if (msgRes != null) {
                        previousHandle?.set(SNACKBAR_MSG_KEY, msgRes)
                    }
                    navController.popBackStack()

                },
                onBackClicked = { navController.popBackStack() },
                screenTitle = entry.arguments?.getString(SaleDestinationsArgs.TITLE_ARG) ?: "!!AddEditSale!!"
            )
        }

        composable(
            route = SaleDestinations.SETTINGS_ROUTE
        ) {
            SettingsScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}

const val SALE_RESULT_KEY = "sale_result"
const val ADD_RESULT_OK = "add_ok"
const val EDIT_RESULT_OK = "edit_ok"