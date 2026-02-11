package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartaDao {

    // CREATE: Para añadir cartas
    @Insert
    suspend fun insertarCarta(carta: Carta): Long

    // READ: Para listar todas las cartas en tu inventario
    @Query("SELECT * FROM tabla_cartas")
    suspend fun obtenerTodas(): List<Carta>

    // UPDATE: Por si quieres cambiar el precio o el estado de una carta
    @Update
    suspend fun actualizarCarta(carta: Carta)
    @Delete
    suspend fun borrarCarta(carta: Carta)
    // DELETE: Para borrar una carta específica usando su idLocal invisible
    @Query("DELETE FROM tabla_cartas")
    suspend fun borrarTodas()
}