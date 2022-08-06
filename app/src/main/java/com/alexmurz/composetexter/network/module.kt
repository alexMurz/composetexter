package com.alexmurz.composetexter.network

import android.util.Log
import com.alexmurz.composetexter.network.connectivity.connectivityModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

private const val BACKEND_BASE_URL = "http://10.0.2.2:8080/"

/**
 * Okhttp and baseline retrofit provider module
 */
val apiModule = module {
    includes(connectivityModule)

    single {

        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .callTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor {
                Log.i("OKHTTP", it)
            }.also {
                it.level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
