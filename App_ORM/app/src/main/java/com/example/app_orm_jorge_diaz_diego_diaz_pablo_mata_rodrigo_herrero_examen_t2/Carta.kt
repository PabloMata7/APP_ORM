package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tabla_cartas")
data class Carta (
    @SerializedName("_id")
    val idMongo: String? = null,

    @PrimaryKey(autoGenerate = true)
    val idLocal: Int = 0,

    @SerializedName("franquicia")
    val franquicia: String = "",

    @SerializedName("coleccion")
    val coleccion: String = "",

    @SerializedName("nombreCarta")
    val nombre: String = "", // Valor por defecto: string vacío

    @SerializedName("rareza")
    val edicion: String = "",

    @SerializedName("estadoConservacion")
    val estado: String = "",

    @SerializedName("precioMercado")
    val valorMercado: Double = 0.0, // Valor por defecto: cero

    @SerializedName("precioVenta")
    val precioVenta: Double = 0.0
)
