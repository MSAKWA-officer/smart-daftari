package com.example.smart_daftari

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- 1. CUSTOMERS (WATEJA) ---
    @GET("customers")
    suspend fun getCustomers(): Response<List<Customer>>

    @POST("customers")
    suspend fun addCustomer(@Body customer: Customer): Response<Unit>

    @PATCH("customers")
    suspend fun updateCustomer(
        @Query("id", encoded = true) id: String, // Utatuma: "eq.$id"
        @Body customer: Customer
    ): Response<Unit>

    @DELETE("customers")
    suspend fun deleteCustomer(
        @Query("id", encoded = true) id: String // Utatuma: "eq.$id"
    ): Response<Unit>


    // --- 2. PRODUCTS (BIDHAA) ---
    @GET("products?select=*") // select=* inahakikisha data zote zinarudi
    suspend fun getProducts(): Response<List<Product>>

    @POST("products")
    suspend fun addProduct(@Body product: Product): Response<Unit>

    @PATCH("products")
    suspend fun updateProduct(
        @Query("id", encoded = true) id: String,
        @Body product: Product
    ): Response<Unit>

    @DELETE("products")
    suspend fun deleteProduct(@Query("id", encoded = true) id: String): Response<Unit>


    // --- 3. DEBTS (MIKOPO) ---
    @GET("debts")
    suspend fun getDebts(): Response<List<Debt>>

    @POST("debts")
    suspend fun createDebt(@Body debt: Debt): Response<Unit>

    @PATCH("debts")
    suspend fun updateDebtRemaining(
        @Query("id", encoded = true) id: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any> // Muhimu kwa Retrofit na Map
    ): Response<Unit>

    @DELETE("debts")
    suspend fun deleteDebt(@Query("id", encoded = true) id: String): Response<Unit>


    // --- 4. PAYMENTS (MALIPO) ---
    @GET("payments")
    suspend fun getPayments(): Response<List<Payment>>

    @POST("payments")
    suspend fun recordPayment(@Body payment: Payment): Response<Unit>

    @DELETE("payments")
    suspend fun deletePayment(@Query("id", encoded = true) id: String): Response<Unit>

    // Query ya Supabase kutafuta malipo ya deni moja: ?debt_id=eq.123
    @GET("payments")
    suspend fun getPaymentsByDebt(
        @Query("debt_id", encoded = true) debtId: String // Hakikisha jina la column ni debt_id (na underscore)
    ): Response<List<Payment>>
}
