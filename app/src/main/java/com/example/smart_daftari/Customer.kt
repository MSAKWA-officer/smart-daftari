package com.example.smart_daftari

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("guarantor_name")
    val guarantorName: String = "",

    @SerializedName("guarantor_phone")
    val guarantorPhone: String = "",

    @SerializedName("credit_score")
    val creditScore: Int = 100, // Alama ya uaminifu

    @SerializedName("debt")
    val debt: Double = 0.0, // Jumla ya deni analodaiwa

    @SerializedName("created_at")
    val createdAt: String? = null
)
