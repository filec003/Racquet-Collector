package com.example.racquetcollector.api


import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val refresh: String, val access: String)

data class RegisterRequest(val username: String, val password: String, val firstName: String, val lastName: String, val email: String)

interface ApiService {
    @POST("accounts/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("accounts/register")
    suspend fun register(@Body request: RegisterRequest)
}
