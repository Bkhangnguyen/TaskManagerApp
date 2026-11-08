package com.example.taskmanager.data.remote.api

import com.example.taskmanager.data.remote.dto.MemberDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Phân hệ Member do Duy phụ trách
interface MemberApiService {

    @GET("members")
    suspend fun getMembers(): Response<List<MemberDto>>

    @POST("members")
    suspend fun createMember(@Body member: MemberDto): Response<MemberDto>
}
