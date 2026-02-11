package com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2

import com.google.gson.annotations.SerializedName

class Carta (
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("_franquicia")
    val franquicia: String? = null,

    @SerializedName("_coleccion")
    val coleccion: String? = null,

    @SerializedName("nombre_carta")
    val nombre: String = "", // Valor por defecto: string vacío

    @SerializedName("edicion_rareza")
    val edicion: String = "",

    @SerializedName("codigo_verificacion")
    val codigo: String = "",

    @SerializedName("estado_conservacion")
    val estado: String = "",

    @SerializedName("valor_mercado")
    val valorMercado: Double = 0.0, // Valor por defecto: cero

    @SerializedName("precio_venta")
    val precioVenta: Double = 0.0
)