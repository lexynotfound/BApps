package com.raihanardila.bapps.core.module

import android.util.Log
import com.raihanardila.bapps.BuildConfig
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.remote.client.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { AuthPreferences(get()) }

    single {
        val authPreferences: AuthPreferences = get()
        Interceptor { chain ->
            val token = authPreferences.getToken()
            val request = if (!token.isNullOrEmpty()) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                // Token null atau kosong, lakukan penanganan di sini
                println("Error: Token is null or empty")
                Log.e("AuthInterceptor", "Error: Token is null or empty $token")
                chain.request() // Lanjutkan permintaan tanpa token
            }
            chain.proceed(request)
        }
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}