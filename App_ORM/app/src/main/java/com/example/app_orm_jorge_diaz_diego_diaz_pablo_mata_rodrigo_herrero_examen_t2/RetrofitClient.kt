package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Si usas un emulador y el servidor está en tu PC, usa 10.0.2.2
    // Si el servidor es real (MongoDB Atlas), pon aquí la URL de tu API
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: TheVaultApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TheVaultApiService::class.java)
    }
}