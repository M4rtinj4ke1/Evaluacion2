package com.example.evaluacion2.data.remote

import com.example.evaluacion2.data.local.OportunidadEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OportunidadApiService {

    @GET("api/oportunidades")
    suspend fun obtenerTodas(): Response<List<OportunidadEntity>>

    @GET("api/oportunidades/{id}")
    suspend fun obtenerPorId(@Path("id") id: Int): Response<OportunidadEntity>

    @POST("api/oportunidades")
    suspend fun insertar(@Body oportunidad: OportunidadEntity): Response<OportunidadEntity>

    @PUT("api/oportunidades/{id}")
    suspend fun actualizar(
        @Path("id") id: Int,
        @Body oportunidad: OportunidadEntity
    ): Response<OportunidadEntity>

    @DELETE("api/oportunidades/{id}")
    suspend fun eliminar(@Path("id") id: Int): Response<Unit>
}
