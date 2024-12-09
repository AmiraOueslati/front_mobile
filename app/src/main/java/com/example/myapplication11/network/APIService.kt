package com.example.myapplication11.network


import com.example.myapplication11.models.LoginRequest
import com.example.myapplication11.models.LoginResponse
import com.example.myapplication11.models.Product
import com.example.myapplication11.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @GET("products/all") // Si vos produits sont accessibles via '/products/all'
    suspend fun getAllProducts(): List<Product>

    @POST("products/create") // Si l'endpoint est '/products/create'
    suspend fun createProduct(@Body product: Product): Response<Unit>

    @POST("api/users/signup")
    suspend fun signUp(@Body user: User): Response<Unit>
    @Headers("Content-Type: application/json")

    @POST("api/users/login") // Si l'endpoint est '/api/users/login'
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @POST("api/users/auth")
    fun authenticate(@Body credentials: Map<String, String>): Call<User>
}





