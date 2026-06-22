package com.example.jobhub.network

import com.example.jobhub.data.model.ApiResponse
import com.example.jobhub.data.model.Application
import com.example.jobhub.data.model.ApplyJobResponse
import com.example.jobhub.data.model.AuthResponse
import com.example.jobhub.data.model.Bookmark
import com.example.jobhub.data.model.JobListing
import com.example.jobhub.data.model.JobResponse
import com.example.jobhub.data.model.DashboardResponse
import com.example.jobhub.data.model.ProfileUpdateResponse
import com.example.jobhub.data.model.ToggleBookmarkResponse
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

    @POST("job-seeker/profile")
    suspend fun updateProfile(
        @Body request: Map<String, String>
    ): Response<ProfileUpdateResponse>

    @Multipart
    @POST("job-seeker/profile")
    suspend fun updateProfileWithFile(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part resume: MultipartBody.Part? = null,
        @Part profilePicture: MultipartBody.Part? = null
    ): Response<ProfileUpdateResponse>

    @DELETE("job-seeker/profile/photo")
    suspend fun deleteProfilePicture(): Response<ProfileUpdateResponse>

    @DELETE("job-seeker/profile/cv")
    suspend fun deleteCv(): Response<ProfileUpdateResponse>

    // Applications
    @GET("job-seeker/applications")
    suspend fun getApplications(): Response<List<Application>>

    @POST("job-seeker/apply/{jobListing}")
    suspend fun applyJob(
        @Path("jobListing") jobId: Int,
        @Body request: Map<String, String> // e.g., cover_letter
    ): Response<ApplyJobResponse>

    @Multipart
    @POST("job-seeker/apply/{jobListing}")
    suspend fun applyJobWithCv(
        @Path("jobListing") jobId: Int,
        @Part("cover_letter") coverLetter: RequestBody,
        @Part cvFile: MultipartBody.Part
    ): Response<ApplyJobResponse>

    @DELETE("job-seeker/applications/{application}")
    suspend fun deleteApplication(@Path("application") applicationId: Int): Response<Map<String, String>>

    // Bookmarks
    @GET("job-seeker/bookmarks")
    suspend fun getBookmarks(): Response<List<Bookmark>>

    @POST("job-seeker/bookmark/{jobListing}")
    suspend fun toggleBookmark(@Path("jobListing") jobId: Int): Response<ToggleBookmarkResponse>
}
