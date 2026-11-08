package com.example.taskmanager.data.remote.api

import com.example.taskmanager.data.remote.dto.CategoryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Phân hệ Category do Khang phụ trách
interface CategoryApiService {

    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @POST("categories")
    suspend fun createCategory(@Body category: CategoryDto): Response<CategoryDto>
}
