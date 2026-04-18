package com.example.smart_daftari

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("debt_id")
    val debtId: String,

    @SerializedName("amount_paid")
    val amountPaid: Double,

    @SerializedName("payment_method")
    val paymentMethod: String = "Cash", // Mfano: Cash, M-Pesa, Bank

    @SerializedName("created_at")
    val createdAt: String? = null // Hii itatoka Supabase (Timestamp)
)
