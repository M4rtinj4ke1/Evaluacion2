package com.example.evaluacion2.data.repository

import com.example.evaluacion2.data.remote.FeriadoApiService
import com.example.evaluacion2.data.remote.FeriadoDto
import com.example.evaluacion2.data.remote.RetrofitClient

/**
 * Repositorio de la API externa de feriados.
 * Se recibe el ApiService por constructor para poder reemplazarlo por un mock en las pruebas unitarias.
 * No guarda nada en Room ni en el microservicio: la API externa no interfiere con nuestros datos.
 */
class FeriadoRepository(
    private val api: FeriadoApiService = RetrofitClient.feriadoApiService
) {

    // Llama a la API externa y devuelve la lista o el error
    suspend fun obtenerFeriados(): Result<List<FeriadoDto>> {
        return try {
            val response = api.obtenerFeriados()
            val body = response.body()
            if (response.isSuccessful && body != null && body.status == "success") {
                Result.success(body.data)
            } else {
                Result.failure(Exception("Error en API externa: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Sin conexión con la API externa"))
        }
    }

    // Deja solo los feriados desde hoy en adelante, ordenados por fecha.
    // Las fechas vienen como "yyyy-MM-dd", así que compararlas como texto respeta el orden cronológico.
    fun filtrarProximos(feriados: List<FeriadoDto>, hoy: String): List<FeriadoDto> =
        feriados.filter { it.fecha >= hoy }.sortedBy { it.fecha }
}
