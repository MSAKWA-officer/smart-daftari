package com.example.smart_daftari

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = "", // Iwe nullable ili kuzuia makosa kama database ina null

    @SerializedName("price")
    val price: Double,

    @SerializedName("stock") // Hili ndilo jina la column kule Supabase
    val stock_quantity: Int = 0 // Hili ndilo jina tutakalotumia kwenye ProductScreen.kt
)
