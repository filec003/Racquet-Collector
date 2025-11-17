package com.example.racquetcollector.api


import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val refresh: String, val access: String)

data class Racquet(
    val id: Int,
    val model_name: String,
    val brand_name: String,
    val model_year: Int,
    val head_size_in2: Int,
    val length_in: Float,
    val unstrung_weight_g: Int,
    val strung_weight_g: Int,
    val swing_weight: Int,
    val twist_weight: Int,
    val balance_mm: Int,
    val mains: Int,
    val crosses: Int
)

data class RacquetResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Racquet>
)


data class RegisterRequest(val username: String, val password: String, val firstName: String, val lastName: String, val email: String)

interface ApiService {
    @POST("accounts/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("accounts/register")
    suspend fun register(@Body request: RegisterRequest)

    @GET("racquets/")
    suspend fun getRacquetsByBrand(
        @Query("brand_name") brandName: String
    ): RacquetResponse
}

