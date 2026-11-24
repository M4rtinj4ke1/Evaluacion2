package com.example.evaluacion2.data.remote

import com.google.gson.annotations.SerializedName

// Respuesta completa de la API externa: {"status": "success", "data": [ ... ]}
data class FeriadosResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<FeriadoDto>
)

// Un feriado de Chile tal como lo entrega la API externa (api.boostr.cl)
data class FeriadoDto(
    @SerializedName("date") val fecha: String,          // formato "yyyy-MM-dd"
    @SerializedName("title") val nombre: String,
    @SerializedName("type") val tipo: String,           // "Civil" o "Religioso"
    @SerializedName("inalienable") val irrenunciable: Boolean,
    @SerializedName("extra") val detalle: String? = null
)
