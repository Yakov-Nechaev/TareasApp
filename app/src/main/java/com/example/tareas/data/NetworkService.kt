package com.example.tareas.data

import com.example.tareas.models.Tarea
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object NetworkService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}


interface ApiService {
    @GET("todos")
    suspend fun getTodos(): List<Tarea>

    @GET("todos")
    suspend fun getById(@Query("userId") userId: Int = 1): List<Tarea>

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int)

    @POST("todos")
    suspend fun createTodo(@Body todo: Tarea): Tarea

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body todo: Tarea): Tarea

}