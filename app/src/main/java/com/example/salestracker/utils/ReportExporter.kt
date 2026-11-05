package com.example.salestracker.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.salestracker.R
import com.example.salestracker.data.model.SaleEventWithItems
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

object ReportExporter {

    fun generateTextReport(
        context: Context,
        clerkName: String,
        periodText: String,
        sales: List<SaleEventWithItems>
    ): String {
        val totalRevenue = sales.sumOf { it.saleEvent.totalSalePrice }
        val totalSalesCount = sales.size
        val totalItemsSold = sales.sumOf { sale -> sale.items.sumOf { it.saleItem.quantity } }

        val productMap = mutableMapOf<String, Pair<Int, Int>>() // productName -> (qty, totalCents)
        sales.forEach { sale ->
            sale.items.forEach { item ->
                val name = item.product.name
                val current = productMap.getOrDefault(name, Pair(0, 0))
                productMap[name] = Pair(
                    current.first + item.saleItem.quantity,
                    current.second + item.saleItem.salePrice
                )
            }
        }

        val sb = StringBuilder()
        sb.appendLine("========================================")
        sb.appendLine("          ${context.getString(R.string.report_header_title)}             ")
        sb.appendLine("========================================")
        if (clerkName.isNotBlank()) {
            sb.appendLine(context.getString(R.string.report_clerk_label, clerkName))
        }
        sb.appendLine(context.getString(R.string.report_period_label, periodText))
        sb.appendLine(context.getString(R.string.report_generated_label, LocalDate.now().toString()))
        sb.appendLine()
        sb.appendLine("----------------------------------------")
        sb.appendLine(context.getString(R.string.summary_section_title).uppercase())
        sb.appendLine("----------------------------------------")
        sb.appendLine(context.getString(R.string.report_total_sales, totalSalesCount))
        sb.appendLine(context.getString(R.string.report_total_income, totalRevenue.toSwiftString()))
        sb.appendLine(context.getString(R.string.report_total_items_sold, totalItemsSold))
        sb.appendLine()

        if (productMap.isNotEmpty()) {
            sb.appendLine("----------------------------------------")
            sb.appendLine(context.getString(R.string.products_breakdown_title).uppercase())
            sb.appendLine("----------------------------------------")
            productMap.forEach { (prodName, pair) ->
                sb.appendLine(context.getString(R.string.report_product_breakdown_item, prodName, pair.first, pair.second.toSwiftString()))
            }
            sb.appendLine()
        }

        sb.appendLine("----------------------------------------")
        sb.appendLine(context.getString(R.string.detailed_sales_title).uppercase())
        sb.appendLine("----------------------------------------")
        if (sales.isEmpty()) {
            sb.appendLine(context.getString(R.string.no_sales_in_period))
        } else {
            sales.forEachIndexed { index, saleWithItems ->
                val sale = saleWithItems.saleEvent
                sb.appendLine(context.getString(R.string.report_receipt_header, index + 1, sale.date, sale.buyer))
                sb.appendLine("   ${context.getString(R.string.total_label)}: $${sale.totalSalePrice.toSwiftString()}")
                sb.appendLine("   ${context.getString(R.string.products_breakdown_title)}:")
                saleWithItems.items.forEach { item ->
                    val unitPrice = if (item.saleItem.quantity > 0) item.saleItem.salePrice / item.saleItem.quantity else 0
                    sb.appendLine(context.getString(R.string.report_receipt_item, item.saleItem.quantity, item.product.name, unitPrice.toSwiftString(), item.saleItem.salePrice.toSwiftString()))
                }
                if (!sale.saleNotes.isNullOrBlank()) {
                    sb.appendLine(context.getString(R.string.report_receipt_notes, sale.saleNotes))
                }
                sb.appendLine()
            }
        }
        sb.appendLine("========================================")
        return sb.toString()
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.report_header_title), text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, context.getString(R.string.report_copied_toast), Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_report_title))
        context.startActivity(shareIntent)
    }

    fun exportPdf(
        context: Context,
        clerkName: String,
        periodText: String,
        sales: List<SaleEventWithItems>
    ) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(24, 90, 150)
            textSize = 18f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val headerPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 13f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val subHeaderPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 40f
        val margin = 40f

        fun checkNewPage(neededHeight: Float) {
            if (y + neededHeight > pageHeight - margin) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = margin
            }
        }

        canvas.drawText(context.getString(R.string.report_header_title), margin, y, titlePaint)
        y += 24f

        if (clerkName.isNotBlank()) {
            canvas.drawText(context.getString(R.string.report_clerk_label, clerkName), margin, y, headerPaint)
            y += 18f
        }
        canvas.drawText(context.getString(R.string.report_period_label, periodText), margin, y, subHeaderPaint)
        y += 15f
        canvas.drawText(context.getString(R.string.report_generated_label, LocalDate.now().toString()), margin, y, subHeaderPaint)
        y += 22f

        canvas.drawLine(margin, y, pageWidth - margin, y, dividerPaint)
        y += 18f

        val totalRevenue = sales.sumOf { it.saleEvent.totalSalePrice }
        val totalSalesCount = sales.size
        val totalItemsSold = sales.sumOf { sale -> sale.items.sumOf { it.saleItem.quantity } }

        checkNewPage(90f)
        canvas.drawText(context.getString(R.string.summary_section_title).uppercase(), margin, y, headerPaint)
        y += 20f
        canvas.drawText(context.getString(R.string.report_total_sales, totalSalesCount), margin + 10f, y, paint)
        y += 16f
        canvas.drawText(context.getString(R.string.report_total_income, totalRevenue.toSwiftString()), margin + 10f, y, paint)
        y += 16f
        canvas.drawText(context.getString(R.string.report_total_items_sold, totalItemsSold), margin + 10f, y, paint)
        y += 22f

        canvas.drawLine(margin, y, pageWidth - margin, y, dividerPaint)
        y += 18f

        val productMap = mutableMapOf<String, Pair<Int, Int>>()
        sales.forEach { sale ->
            sale.items.forEach { item ->
                val name = item.product.name
                val current = productMap.getOrDefault(name, Pair(0, 0))
                productMap[name] = Pair(
                    current.first + item.saleItem.quantity,
                    current.second + item.saleItem.salePrice
                )
            }
        }

        if (productMap.isNotEmpty()) {
            checkNewPage(30f + productMap.size * 18f)
            canvas.drawText(context.getString(R.string.products_breakdown_title).uppercase(), margin, y, headerPaint)
            y += 20f
            productMap.forEach { (prodName, pair) ->
                checkNewPage(18f)
                val line = context.getString(R.string.report_product_breakdown_item_sold, prodName, pair.first, pair.second.toSwiftString())
                canvas.drawText(line, margin + 10f, y, paint)
                y += 18f
            }
            y += 10f
            canvas.drawLine(margin, y, pageWidth - margin, y, dividerPaint)
            y += 18f
        }

        checkNewPage(30f)
        canvas.drawText(context.getString(R.string.detailed_sales_title).uppercase(), margin, y, headerPaint)
        y += 20f

        if (sales.isEmpty()) {
            canvas.drawText(context.getString(R.string.no_sales_in_period), margin + 10f, y, paint)
        } else {
            sales.forEachIndexed { index, saleWithItems ->
                val sale = saleWithItems.saleEvent
                val needed = 50f + saleWithItems.items.size * 16f
                checkNewPage(needed)

                val headerLine = context.getString(R.string.report_receipt_header, index + 1, sale.date, sale.buyer) + " | ${context.getString(R.string.total_label)}: $${sale.totalSalePrice.toSwiftString()}"
                canvas.drawText(headerLine, margin, y, headerPaint)
                y += 18f

                saleWithItems.items.forEach { item ->
                    val unitPrice = if (item.saleItem.quantity > 0) item.saleItem.salePrice / item.saleItem.quantity else 0
                    val itemLine = context.getString(R.string.report_receipt_item, item.saleItem.quantity, item.product.name, unitPrice.toSwiftString(), item.saleItem.salePrice.toSwiftString())
                    canvas.drawText(itemLine, margin + 10f, y, paint)
                    y += 16f
                }

                if (!sale.saleNotes.isNullOrBlank()) {
                    canvas.drawText(context.getString(R.string.report_receipt_notes, sale.saleNotes), margin + 10f, y, subHeaderPaint)
                    y += 16f
                }
                y += 10f
            }
        }

        pdfDocument.finishPage(page)

        val pdfFile = File(context.cacheDir, "SwiftSales_Report_${System.currentTimeMillis()}.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        shareFile(context, pdfFile, "application/pdf")
    }

    fun exportCsv(
        context: Context,
        clerkName: String,
        periodText: String,
        sales: List<SaleEventWithItems>
    ) {
        val csvFile = File(context.cacheDir, "SwiftSales_Report_${System.currentTimeMillis()}.csv")
        csvFile.printWriter().use { out ->
            out.println(context.getString(R.string.report_header_title))
            if (clerkName.isNotBlank()) out.println("\"${context.getString(R.string.report_clerk_label, clerkName)}\"")
            out.println("\"${context.getString(R.string.report_period_label, periodText)}\"")
            out.println("\"${context.getString(R.string.report_generated_label, LocalDate.now().toString())}\"")
            out.println()
            out.println(context.getString(R.string.report_csv_headers))

            sales.forEach { saleWithItems ->
                val sale = saleWithItems.saleEvent
                saleWithItems.items.forEach { item ->
                    val unitPrice = if (item.saleItem.quantity > 0) item.saleItem.salePrice / item.saleItem.quantity else 0
                    val cleanNotes = (sale.saleNotes ?: "").replace("\"", "\"\"")
                    val cleanBuyer = sale.buyer.replace("\"", "\"\"")
                    val cleanProd = item.product.name.replace("\"", "\"\"")

                    out.println("\"${sale.id}\",\"${sale.date}\",\"$cleanBuyer\",\"$${sale.totalSalePrice.toSwiftString()}\",\"$cleanProd\",${item.saleItem.quantity},\"$${unitPrice.toSwiftString()}\",\"$${item.saleItem.salePrice.toSwiftString()}\",\"$cleanNotes\"")
                }
            }
        }
        shareFile(context, csvFile, "text/csv")
    }

    fun exportQuickBooks(
        context: Context,
        clerkName: String,
        sales: List<SaleEventWithItems>
    ) {
        val iifFile = File(context.cacheDir, "SwiftSales_QuickBooks_${System.currentTimeMillis()}.iif")
        iifFile.printWriter().use { out ->
            out.println("!TRNS\tTRNSID\tTRNSTYPE\tDATE\tACCNT\tNAME\tAMOUNT\tDOCNUM\tMEMO")
            out.println("!SPL\tSPLID\tTRNSTYPE\tDATE\tACCNT\tNAME\tAMOUNT\tDOCNUM\tMEMO")
            out.println("!ENDTRNS")

            sales.forEachIndexed { index, saleWithItems ->
                val sale = saleWithItems.saleEvent
                val docNum = 1000 + index
                val totalAmount = String.format(java.util.Locale.US, "%.2f", sale.totalSalePrice / 100.0)
                val memo = if (clerkName.isNotBlank()) "Clerk: $clerkName | ${sale.saleNotes ?: ""}" else sale.saleNotes ?: ""

                out.println("TRNS\t\tSALES RECEIPT\t${sale.date}\tUndeposited Funds\t${sale.buyer}\t$totalAmount\t$docNum\t$memo")

                saleWithItems.items.forEach { item ->
                    val itemAmount = String.format(java.util.Locale.US, "%.2f", -(item.saleItem.salePrice / 100.0))
                    val itemMemo = "${item.product.name} (Qty ${item.saleItem.quantity})"
                    out.println("SPL\t\tSALES RECEIPT\t${sale.date}\tSales Income\t\t$itemAmount\t\t$itemMemo")
                }

                out.println("ENDTRNS")
            }
        }
        shareFile(context, iifFile, "text/plain")
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_report_title))
        context.startActivity(shareIntent)
    }
}
