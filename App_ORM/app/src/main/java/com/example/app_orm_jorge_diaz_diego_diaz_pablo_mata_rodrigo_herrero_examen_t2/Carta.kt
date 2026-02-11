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

    @SerializedName("_franquicia")
    val franquicia: String = "",

    @SerializedName("_coleccion")
    val coleccion: String = "",

    @SerializedName("nombre_carta")
    val nombre: String = "", // Valor por defecto: string vacío

    @SerializedName("edicion_rareza")
    val edicion: String = "",

    @SerializedName("estado_conservacion")
    val estado: String = "",

    @SerializedName("valor_mercado")
    val valorMercado: Double = 0.0, // Valor por defecto: cero

    @SerializedName("precio_venta")
    val precioVenta: Double = 0.0
)