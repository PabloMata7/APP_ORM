package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartaDao {

    // CREATE: para añadir cartas
    @Insert
    suspend fun insertarCarta(carta: Carta): Long

    // READ: para listar todas las cartas en tu inventario
    @Query("SELECT * FROM tabla_cartas")
    suspend fun obtenerTodas(): List<Carta>

    // UPDATE: por si quieres cambiar el precio o el estado de una carta
    @Update
    suspend fun actualizarCarta(carta: Carta)
    @Delete
    suspend fun borrarCarta(carta: Carta)
    // DELETE: para borrar una carta específica usando su idLocal invisible
    @Query("DELETE FROM tabla_cartas")
    suspend fun borrarTodas()
}