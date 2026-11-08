package com.example.taskmanager.data.remote

import com.example.taskmanager.data.remote.api.CategoryApiService
import com.example.taskmanager.data.remote.api.MemberApiService
import com.example.taskmanager.data.remote.api.TaskApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Base URL của REST API (có thể trỏ vào MockAPI, Firebase Functions hoặc Node.js server)
    private const val BASE_URL = "https://663f7360e3a7a32cfeb4ad9a.mockapi.io/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val taskApiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    val categoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }

    val memberApiService: MemberApiService by lazy {
        retrofit.create(MemberApiService::class.java)
    }
}
