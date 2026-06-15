package com.example.jobhub.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String? = null
)

data class JobSeekerProfile(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val resume: String?,
    val skills: String?,
    val experience: String?,
    val education: String?,
    val phone: String?,
    val address: String?
)

data class Company(
    val id: Int,
    @SerializedName("user_id") val userId: Int?,
    val name: String,
    val logo: String?,
    val description: String?,
    val website: String?,
    val location: String?
)

data class Category(
    val id: Int,
    val name: String,
    val slug: String?
)

data class JobListing(
    val id: Int,
    @SerializedName("company_id") val companyId: Int?,
    val title: String,
    val description: String,
    val requirements: String?,
    val location: String,
    val employment_type: String?,
    val work_type: String?,
    val salary_min: Int?,
    val salary_max: Int?,
    val status: String?,
    val company: Company? = null,
    val category: Category? = null
)

data class JobResponse(
    val current_page: Int,
    val data: List<JobListing>,
    val next_page_url: String?,
    val prev_page_url: String?,
    val total: Int
)

data class Application(
    val id: Int,
    @SerializedName("job_listing_id") val jobListingId: Int,
    @SerializedName("job_seeker_id") val jobSeekerId: Int,
    val status: String,
    @SerializedName("cover_letter") val coverLetter: String?,
    @SerializedName("created_at") val createdAt: String?,
    val job_listing: JobListing? = null
)

data class Bookmark(
    val id: Int,
    @SerializedName("job_listing_id") val jobListingId: Int,
    @SerializedName("job_seeker_id") val jobSeekerId: Int,
    val job_listing: JobListing? = null
)

// Dashboard
data class DashboardResponse(
    @SerializedName("total_applications") val totalApplications: Int,
    @SerializedName("waiting_applications") val waitingApplications: Int,
    @SerializedName("accepted_applications") val acceptedApplications: Int,
    @SerializedName("recent_applications") val recentApplications: List<Application>
)

// Auth related requests and responses
data class AuthResponse(
    val message: String?,
    @SerializedName("access_token") val token: String?,
    @SerializedName("token_type") val tokenType: String?,
    val user: User?
)

data class ApiResponse<T>(
    val message: String?,
    val data: T?
)
