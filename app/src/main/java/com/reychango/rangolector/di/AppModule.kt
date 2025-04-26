package com.reychango.rangolector.di

import com.reychango.rangolector.data.services.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideTodosTusLibrosService(): TodosTusLibrosService {
        return TodosTusLibrosService()
    }

    @Provides
    @Singleton
    fun provideCasaDelLibroService(): CasaDelLibroService {
        return CasaDelLibroService()
    }

    @Provides
    @Singleton
    fun provideUnifiedSpanishBookService(
        todosTusLibrosService: TodosTusLibrosService,
        casaDelLibroService: CasaDelLibroService
    ): UnifiedSpanishBookService {
        return UnifiedSpanishBookService(todosTusLibrosService, casaDelLibroService)
    }

    @Provides
    @Singleton
    fun provideOpenLibraryService(okHttpClient: OkHttpClient): OpenLibraryService {
        return Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryService::class.java)
    }
} 