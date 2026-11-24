package com.example.evaluacion2.data.remote

import retrofit2.Response
import retrofit2.http.GET

// API EXTERNA (de terceros): feriados de Chile de api.boostr.cl
// Solo lectura (GET). No es nuestra, no podemos crear ni modificar datos en ella.
interface FeriadoApiService {

    // GET https://api.boostr.cl/holidays.json
    @GET("holidays.json")
    suspend fun obtenerFeriados(): Response<FeriadosResponse>
}
