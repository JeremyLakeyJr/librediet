package com.librediet.app.di

import android.content.Context
import androidx.room.Room
import com.librediet.app.data.local.FoodItemDao
import com.librediet.app.data.local.LibreDietDatabase
import com.librediet.app.data.local.MealDao
import com.librediet.app.data.local.MealTemplateDao
import com.librediet.app.data.local.NutritionGoalsDao
import com.librediet.app.data.remote.OpenFoodFactsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): LibreDietDatabase {
        return Room.databaseBuilder(
            context,
            LibreDietDatabase::class.java,
            LibreDietDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodItemDao(database: LibreDietDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    @Singleton
    fun provideMealDao(database: LibreDietDatabase): MealDao {
        return database.mealDao()
    }

    @Provides
    @Singleton
    fun provideMealTemplateDao(database: LibreDietDatabase): MealTemplateDao {
        return database.mealTemplateDao()
    }

    @Provides
    @Singleton
    fun provideNutritionGoalsDao(database: LibreDietDatabase): NutritionGoalsDao {
        return database.nutritionGoalsDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "LibreDiet Android App - https://github.com/librediet")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(OpenFoodFactsApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(retrofit: Retrofit): OpenFoodFactsApi {
        return retrofit.create(OpenFoodFactsApi::class.java)
    }
}
