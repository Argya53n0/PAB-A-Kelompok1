package com.example.jobhub.network

import com.example.jobhub.data.model.ApiResponse
import com.example.jobhub.data.model.Application
import com.example.jobhub.data.model.AuthResponse
import com.example.jobhub.data.model.Bookmark
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.data.model.JobResponse
import com.example.jobhub.data.model.DashboardResponse
import com.example.jobhub.data.model.JobSeekerProfile
import com.example.jobhub.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: Map<String, String>): Response<ApiResponse<Any>>

    @POST("login")
    suspend fun login(@Body request: Map<String, String>): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    @GET("me")
    suspend fun getMe(): Response<User>

    @GET("jobs")
    suspend fun getJobs(): Response<JobResponse>

    @GET("jobs/{id}")
    suspend fun getJobDetail(@Path("id") id: Int): Response<JobListing>

    // Job Seeker Profile & Dashboard
    @GET("job-seeker/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>

    @GET("job-seeker/profile")
    suspend fun getProfile(): Response<User>

    @Multipart
    @POST("job-seeker/profile")
    suspend fun updateProfile(
        @Part("skills") skills: RequestBody?,
        @Part("experience") experience: RequestBody?,
        @Part("education") education: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part resume: MultipartBody.Part?
    ): Response<ProfileUpdateResponse>

    // Applications
    @GET("job-seeker/applications")
    suspend fun getApplications(): Response<List<Application>>

    @POST("job-seeker/apply/{jobListing}")
    suspend fun applyJob(
        @Path("jobListing") jobId: Int,
        @Body request: Map<String, String> // e.g., cover_letter
    ): Response<ApplyJobResponse>

    @DELETE("job-seeker/applications/{application}")
    suspend fun deleteApplication(@Path("application") applicationId: Int): Response<Map<String, String>>

    // Bookmarks
    @GET("job-seeker/bookmarks")
    suspend fun getBookmarks(): Response<List<Bookmark>>

    @POST("job-seeker/bookmark/{jobListing}")
    suspend fun toggleBookmark(@Path("jobListing") jobId: Int): Response<ToggleBookmarkResponse>
}
