package com.example.evaluacion2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa un contacto corporativo en la base de datos
@Entity(tableName = "contactos")
data class ContactoEntity(

    // Identificador único del contacto
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val nombre: String,

    // Identificador de la organización a la que pertenece
    val organizacionId: Int,

    val cargo: String,
    val area: String,
    val correo: String,
    val telefono: String,
    val medioContacto: String,
    val estado: String,
    val observaciones: String
)
