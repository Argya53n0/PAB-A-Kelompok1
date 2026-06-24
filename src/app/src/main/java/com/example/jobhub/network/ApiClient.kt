package com.example.jobhub.network

import com.example.jobhub.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // PERHATIAN: Menggunakan IP lokal laptop agar bisa diakses dari HP fisik via WiFi
    // Pastikan laptop dan HP terhubung ke jaringan WiFi yang sama
    // Untuk emulator, ganti ke: http://10.0.2.2:8000/api/
    private val BASE_URL: String = "http://192.168.100.203:8000/api/"

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
            
            // Accept JSON responses
            requestBuilder.addHeader("Accept", "application/json")
            
            // Hanya set Content-Type JSON untuk request non-multipart
            // Untuk multipart (upload file), Content-Type di-set otomatis oleh OkHttp
            if (req.body !is okhttp3.MultipartBody) {
                requestBuilder.addHeader("Content-Type", "application/json")
            }

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
