package com.example.salestracker.ui.screens.stats

sealed interface StatsAction {
    data class SelectFilter(val filterType: DateFilterType) : StatsAction
    data class UpdateStartDate(val date: String) : StatsAction
    data class UpdateEndDate(val date: String) : StatsAction
    data class UpdateClerkName(val name: String) : StatsAction
    object ShowExportDialog : StatsAction
    object HideExportDialog : StatsAction
}
