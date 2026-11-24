package com.example.evaluacion2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localidades")
data class LocalidadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val region: String,
    val comuna: String
)