package com.example.salestracker.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.R
import com.example.salestracker.SalesListItem
import com.example.salestracker.ui.components.DatePicker
import com.example.salestracker.ui.components.SalesAppBar
import com.example.salestracker.utils.ReportExporter
import com.example.salestracker.utils.toSwiftString
import com.example.salestracker.viewModel.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavIconClick: () -> Unit = {},
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiState.showExportDialog) {
        ExportOptionsDialog(
            onDismiss = { viewModel.onAction(StatsAction.HideExportDialog) },
            onCopyText = {
                ReportExporter.copyToClipboard(context, uiState.reportText)
                viewModel.onAction(StatsAction.HideExportDialog)
            },
            onShareText = {
                ReportExporter.shareText(context, uiState.reportText)
                viewModel.onAction(StatsAction.HideExportDialog)
            },
            onExportPdf = {
                val periodText = viewModel.getPeriodText(uiState.filterType, uiState.startDate, uiState.endDate)
                ReportExporter.exportPdf(
                    context = context,
                    clerkName = uiState.clerkName,
                    periodText = periodText,
                    sales = uiState.filteredSaleEventWithItems
                )
                viewModel.onAction(StatsAction.HideExportDialog)
            },
            onExportCsv = {
                val periodText = viewModel.getPeriodText(uiState.filterType, uiState.startDate, uiState.endDate)
                ReportExporter.exportCsv(
                    context = context,
                    clerkName = uiState.clerkName,
                    periodText = periodText,
                    sales = uiState.filteredSaleEventWithItems
                )
                viewModel.onAction(StatsAction.HideExportDialog)
            },
            onExportQuickBooks = {
                ReportExporter.exportQuickBooks(
                    context = context,
                    clerkName = uiState.clerkName,
                    sales = uiState.filteredSaleEventWithItems
                )
                viewModel.onAction(StatsAction.HideExportDialog)
            }
        )
    }

    Scaffold(
        topBar = {
            SalesAppBar(
                title = stringResource(R.string.stats_title),
                navigationIcon = {
                    IconButton(onClick = onNavIconClick) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = stringResource(R.string.open_nav_drawer_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        StatsContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun StatsContent(
    modifier: Modifier = Modifier,
    uiState: StatsUIState,
    onAction: (StatsAction) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.filterType == DateFilterType.THIS_MONTH,
                onClick = { onAction(StatsAction.SelectFilter(DateFilterType.THIS_MONTH)) },
                label = { Text(stringResource(R.string.filter_this_month)) }
            )
            FilterChip(
                selected = uiState.filterType == DateFilterType.THIS_WEEK,
                onClick = { onAction(StatsAction.SelectFilter(DateFilterType.THIS_WEEK)) },
                label = { Text(stringResource(R.string.filter_this_week)) }
            )
            FilterChip(
                selected = uiState.filterType == DateFilterType.TODAY,
                onClick = { onAction(StatsAction.SelectFilter(DateFilterType.TODAY)) },
                label = { Text(stringResource(R.string.filter_today)) }
            )
            FilterChip(
                selected = uiState.filterType == DateFilterType.ALL_TIME,
                onClick = { onAction(StatsAction.SelectFilter(DateFilterType.ALL_TIME)) },
                label = { Text(stringResource(R.string.filter_all_time)) }
            )
            FilterChip(
                selected = uiState.filterType == DateFilterType.CUSTOM,
                onClick = { onAction(StatsAction.SelectFilter(DateFilterType.CUSTOM)) },
                label = { Text(stringResource(R.string.filter_custom)) }
            )
        }

        if (uiState.filterType == DateFilterType.CUSTOM) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatePicker(
                    value = uiState.startDate,
                    onValueChange = { onAction(StatsAction.UpdateStartDate(it)) },
                    label = "Start Date",
                    modifier = Modifier.weight(1f)
                )
                DatePicker(
                    value = uiState.endDate,
                    onValueChange = { onAction(StatsAction.UpdateEndDate(it)) },
                    label = "End Date",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optional Clerk Name TextField
        OutlinedTextField(
            value = uiState.clerkName,
            onValueChange = { onAction(StatsAction.UpdateClerkName(it)) },
            label = { Text(stringResource(R.string.clerk_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Metric Cards Section
        Text(
            text = stringResource(R.string.summary_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.total_revenue_label),
                value = "$${uiState.totalRevenueCents.toSwiftString()}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.total_sales_count_label),
                value = "${uiState.totalSalesCount}",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.total_items_sold_label),
                value = "${uiState.totalItemsSold}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Product Breakdown Section
        if (uiState.productBreakdown.isNotEmpty()) {
            Text(
                text = stringResource(R.string.products_breakdown_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    uiState.productBreakdown.forEachIndexed { index, prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${prod.productName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${prod.totalQuantity} sold ($${prod.totalRevenueCents.toSwiftString()})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (index < uiState.productBreakdown.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Detailed Receipts Header & Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.detailed_sales_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = { onAction(StatsAction.ShowExportDialog) }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(stringResource(R.string.export_data_action))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.filteredSaleEventWithItems.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_sales_in_period),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            uiState.filteredSaleEventWithItems.forEach { productSale ->
                SalesListItem(
                    saleEventWithItems = productSale,
                    isSelected = false,
                    onClick = { },
                    onLongClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Report Preview Section
        Text(
            text = stringResource(R.string.report_preview_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = uiState.reportText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ExportOptionsDialog(
    onDismiss: () -> Unit,
    onCopyText: () -> Unit,
    onShareText: () -> Unit,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit,
    onExportQuickBooks: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.export_dialog_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onCopyText,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.export_copy_text))
                    }
                }

                TextButton(
                    onClick = onShareText,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.export_share_text))
                    }
                }

                TextButton(
                    onClick = onExportPdf,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.export_pdf))
                    }
                }

                TextButton(
                    onClick = onExportCsv,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.export_csv))
                    }
                }

                TextButton(
                    onClick = onExportQuickBooks,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.export_quickbooks))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_action))
            }
        }
    )
}
