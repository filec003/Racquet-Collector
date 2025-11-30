package com.example.racquetcollector.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    fun getClient(token: String? = null): ApiService {
        val client = OkHttpClient.Builder().apply {
            token?.let { addInterceptor(AuthInterceptor(it)) }
        }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}