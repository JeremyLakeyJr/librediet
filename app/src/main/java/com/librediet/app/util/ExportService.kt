package com.librediet.app.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.librediet.app.data.model.ExportFormat
import com.librediet.app.data.model.ExportOptions
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.NutritionSummary
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportService @Inject constructor() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun exportMeals(
        context: Context,
        meals: List<Meal>,
        summary: NutritionSummary,
        options: ExportOptions
    ): Uri? {
        return when (options.format) {
            ExportFormat.CSV -> exportToCsv(context, meals, summary, options)
            ExportFormat.PDF -> exportToPdf(context, meals, summary, options)
        }
    }

    private fun exportToCsv(
        context: Context,
        meals: List<Meal>,
        summary: NutritionSummary,
        options: ExportOptions
    ): Uri? {
        val fileName = "librediet_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, "exports/$fileName")
        file.parentFile?.mkdirs()

        try {
            FileWriter(file).use { writer ->
                // Header
                writer.append("Date,Time,Category,Food Name,Quantity,Unit,Calories,Protein (g),Carbs (g),Fat (g),Fiber (g),Sugar (g),Sodium (mg),Notes\n")

                // Data rows
                meals.forEach { meal ->
                    writer.append("${meal.timestamp.format(dateFormatter)},")
                    writer.append("${meal.timestamp.toLocalTime()},")
                    writer.append("${meal.category.displayName},")
                    writer.append("\"${meal.foodName.replace("\"", "\"\"")}\",")
                    writer.append("${meal.quantity},")
                    writer.append("${meal.unit},")
                    writer.append("${meal.calories},")
                    writer.append("${meal.protein},")
                    writer.append("${meal.carbohydrates},")
                    writer.append("${meal.fat},")
                    writer.append("${meal.fiber},")
                    writer.append("${meal.sugar},")
                    writer.append("${meal.sodium},")
                    writer.append("\"${(meal.notes ?: "").replace("\"", "\"\"")}\"\n")
                }

                // Summary section
                if (options.includeNutritionSummary) {
                    writer.append("\n")
                    writer.append("NUTRITION SUMMARY\n")
                    writer.append("Period,${options.startDate.format(dateFormatter)} to ${options.endDate.format(dateFormatter)}\n")
                    writer.append("Total Meals,${summary.mealCount}\n")
                    writer.append("Total Calories,${summary.totalCalories}\n")
                    writer.append("Total Protein (g),${summary.totalProtein}\n")
                    writer.append("Total Carbs (g),${summary.totalCarbohydrates}\n")
                    writer.append("Total Fat (g),${summary.totalFat}\n")
                    writer.append("Total Fiber (g),${summary.totalFiber}\n")
                    writer.append("Total Sugar (g),${summary.totalSugar}\n")
                    writer.append("Total Sodium (mg),${summary.totalSodium}\n")
                }
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun exportToPdf(
        context: Context,
        meals: List<Meal>,
        summary: NutritionSummary,
        options: ExportOptions
    ): Uri? {
        val fileName = "librediet_export_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, "exports/$fileName")
        file.parentFile?.mkdirs()

        try {
            PdfWriter(file).use { writer ->
                PdfDocument(writer).use { pdfDoc ->
                    Document(pdfDoc).use { document ->
                        // Title
                        document.add(
                            Paragraph("LibreDiet - Meal Report")
                                .setFontSize(20f)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(10f)
                        )

                        // Date range
                        document.add(
                            Paragraph("Period: ${options.startDate.format(dateFormatter)} to ${options.endDate.format(dateFormatter)}")
                                .setFontSize(12f)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(20f)
                        )

                        // Nutrition Summary
                        if (options.includeNutritionSummary) {
                            document.add(
                                Paragraph("Nutrition Summary")
                                    .setFontSize(14f)
                                    .setBold()
                                    .setMarginBottom(10f)
                            )

                            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                                .useAllAvailableWidth()

                            summaryTable.addCell(Cell().add(Paragraph("Total Meals")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.mealCount}")))
                            summaryTable.addCell(Cell().add(Paragraph("Total Calories")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.totalCalories.toInt()} kcal")))
                            summaryTable.addCell(Cell().add(Paragraph("Total Protein")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.totalProtein.toInt()} g")))
                            summaryTable.addCell(Cell().add(Paragraph("Total Carbohydrates")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.totalCarbohydrates.toInt()} g")))
                            summaryTable.addCell(Cell().add(Paragraph("Total Fat")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.totalFat.toInt()} g")))
                            summaryTable.addCell(Cell().add(Paragraph("Total Fiber")))
                            summaryTable.addCell(Cell().add(Paragraph("${summary.totalFiber.toInt()} g")))

                            document.add(summaryTable)
                            document.add(Paragraph("\n"))
                        }

                        // Meal Details
                        if (options.includeMealDetails && meals.isNotEmpty()) {
                            document.add(
                                Paragraph("Meal Details")
                                    .setFontSize(14f)
                                    .setBold()
                                    .setMarginBottom(10f)
                            )

                            val mealTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1.5f, 3f, 1f, 1f, 1f, 1f)))
                                .useAllAvailableWidth()

                            // Headers
                            listOf("Date/Time", "Category", "Food", "Qty", "Cal", "P", "C").forEach {
                                mealTable.addHeaderCell(Cell().add(Paragraph(it).setBold().setFontSize(10f)))
                            }

                            // Data
                            meals.forEach { meal ->
                                mealTable.addCell(Cell().add(Paragraph(meal.timestamp.format(dateTimeFormatter)).setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph(meal.category.displayName).setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph(meal.foodName).setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph("${meal.quantity} ${meal.unit}").setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph("${meal.calories.toInt()}").setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph("${meal.protein.toInt()}").setFontSize(8f)))
                                mealTable.addCell(Cell().add(Paragraph("${meal.carbohydrates.toInt()}").setFontSize(8f)))
                            }

                            document.add(mealTable)
                        }

                        // Footer
                        document.add(
                            Paragraph("\nGenerated by LibreDiet")
                                .setFontSize(8f)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginTop(20f)
                        )
                    }
                }
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
