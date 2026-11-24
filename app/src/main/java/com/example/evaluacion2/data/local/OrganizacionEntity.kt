package com.example.evaluacion2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa una organización en la base de datos
@Entity(tableName = "organizaciones")
data class OrganizacionEntity(

    // Identificador único de la organización
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val nombre: String,
    val tipo: String,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String,
    val observaciones: String
)