package com.librediet.app.data.remote

import com.librediet.app.data.model.FoodItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Open Food Facts API.
 * Documentation: https://wiki.openfoodfacts.org/API
 */
interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = PRODUCT_FIELDS
    ): Response<ProductResponse>

    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") searchTerms: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = PRODUCT_FIELDS
    ): Response<SearchResponse>

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
        const val PRODUCT_FIELDS = "code,product_name,brands,serving_size,nutriments,image_url"
    }
}

data class ProductResponse(
    val status: Int,
    val status_verbose: String?,
    val product: Product?
)

data class SearchResponse(
    val count: Int,
    val page: Int,
    val page_size: Int,
    val products: List<Product>?
)

data class Product(
    val code: String?,
    val product_name: String?,
    val brands: String?,
    val serving_size: String?,
    val nutriments: Nutriments?,
    val image_url: String?
)

data class Nutriments(
    val energy_kcal_100g: Float?,
    val energy_kcal_serving: Float?,
    val proteins_100g: Float?,
    val proteins_serving: Float?,
    val carbohydrates_100g: Float?,
    val carbohydrates_serving: Float?,
    val fat_100g: Float?,
    val fat_serving: Float?,
    val fiber_100g: Float?,
    val fiber_serving: Float?,
    val sugars_100g: Float?,
    val sugars_serving: Float?,
    val sodium_100g: Float?,
    val sodium_serving: Float?,
    val saturated_fat_100g: Float?,
    val cholesterol_100g: Float?,
    val potassium_100g: Float?,
    val vitamin_a_100g: Float?,
    val vitamin_c_100g: Float?,
    val calcium_100g: Float?,
    val iron_100g: Float?
)

/**
 * Extension function to convert API Product to local FoodItem model.
 */
fun Product.toFoodItem(): FoodItem? {
    val name = this.product_name ?: return null
    
    return FoodItem(
        name = name,
        brand = this.brands,
        barcode = this.code,
        servingSize = parseServingSize(this.serving_size),
        servingUnit = parseServingUnit(this.serving_size),
        calories = this.nutriments?.energy_kcal_100g ?: 0f,
        protein = this.nutriments?.proteins_100g ?: 0f,
        carbohydrates = this.nutriments?.carbohydrates_100g ?: 0f,
        fat = this.nutriments?.fat_100g ?: 0f,
        fiber = this.nutriments?.fiber_100g ?: 0f,
        sugar = this.nutriments?.sugars_100g ?: 0f,
        sodium = (this.nutriments?.sodium_100g ?: 0f) * 1000, // Convert g to mg
        saturatedFat = this.nutriments?.saturated_fat_100g ?: 0f,
        cholesterol = this.nutriments?.cholesterol_100g ?: 0f,
        potassium = this.nutriments?.potassium_100g ?: 0f,
        vitaminA = this.nutriments?.vitamin_a_100g ?: 0f,
        vitaminC = this.nutriments?.vitamin_c_100g ?: 0f,
        calcium = this.nutriments?.calcium_100g ?: 0f,
        iron = this.nutriments?.iron_100g ?: 0f,
        imageUrl = this.image_url,
        isCustom = false
    )
}

private fun parseServingSize(servingSize: String?): Float {
    if (servingSize.isNullOrBlank()) return 100f
    // Extract numeric value from string like "100g" or "1 cup (240ml)"
    val regex = Regex("""(\d+\.?\d*)""")
    return regex.find(servingSize)?.value?.toFloatOrNull() ?: 100f
}

private fun parseServingUnit(servingSize: String?): String {
    if (servingSize.isNullOrBlank()) return "g"
    // Extract unit from string
    val regex = Regex("""[a-zA-Z]+""")
    return regex.find(servingSize)?.value?.lowercase() ?: "g"
}
