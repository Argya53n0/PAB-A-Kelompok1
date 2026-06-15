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
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val description: String?,
    val logo: String?,
    val website: String?,
    val industry: String?,
    val location: String?
)

data class JobListing(
    val id: Int,
    @SerializedName("company_id") val companyId: Int,
    val title: String,
    val description: String,
    val requirements: String?,
    val salary: String?,
    val location: String?,
    @SerializedName("job_type") val jobType: String?,
    val status: String?,
    val company: Company? = null
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

// Auth related requests and responses
data class AuthResponse(
    val message: String,
    val token: String?,
    val user: User?
)

data class ApiResponse<T>(
    val message: String?,
    val data: T?
)
