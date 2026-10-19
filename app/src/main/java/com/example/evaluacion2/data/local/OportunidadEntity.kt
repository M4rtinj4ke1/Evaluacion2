package com.example.evaluacion2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity le dice a Room que esta clase es una tabla llamada "oportunidades"
@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    // Número único de cada oportunidad. Room lo asigna solo (1, 2, 3...)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fuente: String,
    val prioridad: String,
    val potencial: String,
    val estado: String,
    val observaciones: String,
    // Foto de evidencia (dirección de la imagen guardada) y ubicación. Null = no se registró
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
