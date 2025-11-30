package com.example.racquetcollector.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val refresh: String, val access: String)

@Parcelize
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
    val twist_weight: Float,
    val balance_mm: Int,
    val mains: Int,
    val crosses: Int
) : Parcelable

data class RacquetResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Racquet>
)

data class UserProfile(
    val first_name: String,
    val last_name: String,
    val email: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

// For Collection Management
data class RacquetCollectionItem(
    val id: Int, // ID of the collection entry
    val racquet: Racquet,
    val notes: String?
)

data class AddToCollectionRequest(
    val racquet_id: Int,
    val notes: String?
)


interface ApiService {
    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @POST("accounts/register/")
    suspend fun register(@Body request: RegisterRequest)

    @GET("racquets/")
    suspend fun getRacquetsByBrand(
        @Query("brand_name") brandName: String
    ): RacquetResponse

    @GET
    suspend fun getRacquetsNextPage(@Url url: String): RacquetResponse

    @GET("accounts/profile/")
    suspend fun getProfile(): UserProfile

    // Collection endpoints
    @GET("accounts/collection/")
    suspend fun getCollection(): List<RacquetCollectionItem>

    @POST("accounts/collection/")
    suspend fun addToCollection(@Body request: AddToCollectionRequest): RacquetCollectionItem

    @DELETE("accounts/collection/{id}/")
    suspend fun removeFromCollection(@Path("id") collectionId: Int)
}

class AuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
