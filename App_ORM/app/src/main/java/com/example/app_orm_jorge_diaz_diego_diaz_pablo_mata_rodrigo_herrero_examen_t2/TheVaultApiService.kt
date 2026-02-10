package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import retrofit2.Response
import retrofit2.http.*

interface TheVaultApiService {
    // GET: Para obtener todas las cartas de la colección de MongoDB
    @GET("cartas")
    suspend fun obtenerTodasLasCartas(): Response<List<Carta>>

    // POST: Para enviar una nueva carta y guardarla en la base de datos
    @POST("cartas")
    suspend fun insertarCarta(@Body carta: Carta): Response<Carta>

    // DELETE: Para borrar una carta por su ID único de MongoDB
    @DELETE("cartas/{id}")
    suspend fun eliminarCarta(@Path("id") id: String): Response<Unit>

    // PUT: Para actualizar los datos de una carta existente
    @PUT("cartas/{id}")
    suspend fun actualizarCarta(@Path("id") id: String, @Body carta: Carta): Response<Carta>
}