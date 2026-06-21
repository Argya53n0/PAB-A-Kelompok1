package com.example.jobhub.network

import com.example.jobhub.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Deteksi apakah dijalankan di Emulator atau Device Fisik
    private val isEmulator: Boolean
        get() = android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk" == android.os.Build.PRODUCT
                || android.os.Build.PRODUCT.contains("sdk_gphone")

    // Gunakan 10.0.2.2 untuk Emulator (otomatis tembus ke localhost PC)
    // Gunakan 127.0.0.1 untuk Fisik (dengan bantuan adb reverse)
    private val BASE_URL: String
        get() = if (isEmulator) "http://10.0.2.2:8000/api/" else "http://127.0.0.1:8000/api/"

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
            
            // Memastikan server menerima dan mengirim JSON
            requestBuilder.addHeader("Accept", "application/json")
            requestBuilder.addHeader("Content-Type", "application/json")

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
