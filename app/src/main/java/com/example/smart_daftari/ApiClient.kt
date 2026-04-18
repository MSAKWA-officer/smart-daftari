package com.example.smart_daftari

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://ndmuzhljwyzlwtfntdsj.supabase.co/rest/v1/"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5kbXV6aGxqd3l6bHd0Zm50ZHNqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzYwNzAzODQsImV4cCI6MjA5MTY0NjM4NH0.s-M29eD7X9UG4iUovgGg_L8ykkbMy_L7lbB5SdB1fzU"

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json") // Lazima iwepo kwa POST/PATCH
                .addHeader("Prefer", "return=representation") // Inasaidia kurudisha data iliyofanyiwa kazi
                .build()
            chain.proceed(request)
        })
        .build()


    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 🔥 muhimu sana
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}