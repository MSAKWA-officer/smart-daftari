package com.example.smart_daftari

import com.google.gson.annotations.SerializedName

data class Debt(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("customer_id")
    val customerId: String,

    @SerializedName("product_id")
    val productId: String,

    @SerializedName("amount_borrowed")
    val amountBorrowed: Double,

    @SerializedName("remaining_amount")
    val remainingAmount: Double,

    @SerializedName("due_date")
    val dueDate: String? = null, // Mpya: Kwa ajili ya tarehe ya mwisho wa mkopo

    @SerializedName("created_at")
    val createdAt: String? = null
)
