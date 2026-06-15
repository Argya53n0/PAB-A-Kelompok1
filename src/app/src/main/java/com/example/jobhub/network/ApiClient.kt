package com.example.jobhub.network

import com.example.jobhub.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // For android emulator, localhost is 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    fun getApiService(sessionManager: SessionManager): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestBuilder = req.newBuilder()

            // Attach bearer token if available
            sessionManager.fetchAuthToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            
            // Required for Laravel API to return JSON instead of redirecting to login page
            requestBuilder.addHeader("Accept", "application/json")

            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
