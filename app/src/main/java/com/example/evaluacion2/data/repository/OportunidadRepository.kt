package com.example.evaluacion2.data.repository

import android.util.Log
import com.example.evaluacion2.data.local.OportunidadDao
import com.example.evaluacion2.data.local.OportunidadEntity
import com.example.evaluacion2.data.remote.OportunidadApiService
import com.example.evaluacion2.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio de Oportunidades:
 * Combina la API REST remota (Spring Boot + MySQL) con la base de datos local Room.
 * Actúa bajo el patrón Single Source of Truth: los cambios se sincronizan en la API
 * y en Room, permitiendo que la UI reaccione de inmediato y funcione con o sin conexión.
 */
class OportunidadRepository(
    private val dao: OportunidadDao,
    // Se recibe por constructor para poder reemplazarlo por un mock (MockK) en las pruebas unitarias
    private val api: OportunidadApiService = RetrofitClient.oportunidadApiService
) {

    // Flujo observado por el ViewModel (respaldado por Room)
    val oportunidades: Flow<List<OportunidadEntity>> = dao.obtenerTodas()

    /**
     * Sincroniza todas las oportunidades desde la API de Spring Boot hacia Room.
     */
    suspend fun refrescarDesdeApi(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerTodas()
            if (response.isSuccessful && response.body() != null) {
                val listaRemota = response.body()!!
                dao.limpiarTodas()
                for (item in listaRemota) {
                    dao.insertar(item)
                }
                Result.success(Unit)
            } else {
                val errorMsg = "Error en respuesta API: ${response.code()}"
                Log.w("OportunidadRepo", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("OportunidadRepo", "Fallo al conectar con la API: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene una oportunidad por su ID, intentando primero la API y respaldando en Room.
     */
    suspend fun obtenerPorId(id: Int): OportunidadEntity? = withContext(Dispatchers.IO) {
        try {
            val response = api.obtenerPorId(id)
            if (response.isSuccessful && response.body() != null) {
                val remota = response.body()!!
                dao.insertar(remota)
                return@withContext remota
            }
        } catch (e: Exception) {
            Log.w("OportunidadRepo", "No se pudo consultar API para id $id, usando local: ${e.message}")
        }
        dao.obtenerPorId(id)
    }

    /**
     * Inserta una nueva oportunidad en la API de Spring Boot y luego la persiste en Room con su ID generado.
     */
    suspend fun insertar(oportunidad: OportunidadEntity): Result<OportunidadEntity> = withContext(Dispatchers.IO) {
        try {
            val response = api.insertar(oportunidad)
            if (response.isSuccessful && response.body() != null) {
                val guardada = response.body()!!
                dao.insertar(guardada)
                return@withContext Result.success(guardada)
            } else {
                Log.w("OportunidadRepo", "API respondió ${response.code()}, guardando localmente...")
            }
        } catch (e: Exception) {
            Log.e("OportunidadRepo", "Error de red al insertar en API: ${e.message}. Guardando localmente...")
        }
        // Fallback local en caso de error de red
        dao.insertar(oportunidad)
        Result.success(oportunidad)
    }

    /**
     * Actualiza la oportunidad en la API y en Room.
     */
    suspend fun actualizar(oportunidad: OportunidadEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.actualizar(oportunidad.id, oportunidad)
            if (response.isSuccessful && response.body() != null) {
                dao.actualizar(response.body()!!)
                return@withContext Result.success(Unit)
            } else {
                Log.w("OportunidadRepo", "API respondió ${response.code()} al actualizar")
            }
        } catch (e: Exception) {
            Log.e("OportunidadRepo", "Error de red al actualizar en API: ${e.message}")
        }
        dao.actualizar(oportunidad)
        Result.success(Unit)
    }

    /**
     * Elimina la oportunidad tanto en la API como en Room.
     */
    suspend fun eliminar(oportunidad: OportunidadEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.eliminar(oportunidad.id)
            if (!response.isSuccessful) {
                Log.w("OportunidadRepo", "API respondió ${response.code()} al eliminar")
            }
        } catch (e: Exception) {
            Log.e("OportunidadRepo", "Error de red al eliminar en API: ${e.message}")
        }
        dao.eliminar(oportunidad)
        Result.success(Unit)
    }
}
