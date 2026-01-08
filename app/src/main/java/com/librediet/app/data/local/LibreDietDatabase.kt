package com.librediet.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.librediet.app.data.model.FoodItem
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.MealCategory
import com.librediet.app.data.model.MealTemplate
import com.librediet.app.data.model.NutritionGoals
import com.librediet.app.data.model.TemplateItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [
        FoodItem::class,
        Meal::class,
        MealTemplate::class,
        NutritionGoals::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LibreDietDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun mealDao(): MealDao
    abstract fun mealTemplateDao(): MealTemplateDao
    abstract fun nutritionGoalsDao(): NutritionGoalsDao

    companion object {
        const val DATABASE_NAME = "librediet_database"
    }
}

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val gson = Gson()

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromMealCategory(value: MealCategory): String {
        return value.name
    }

    @TypeConverter
    fun toMealCategory(value: String): MealCategory {
        return MealCategory.valueOf(value)
    }

    @TypeConverter
    fun fromTemplateItemList(items: List<TemplateItem>?): String? {
        return items?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toTemplateItemList(value: String?): List<TemplateItem>? {
        return value?.let {
            val type = object : TypeToken<List<TemplateItem>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
