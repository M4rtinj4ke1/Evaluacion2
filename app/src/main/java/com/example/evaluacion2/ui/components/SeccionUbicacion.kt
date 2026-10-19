package com.example.evaluacion2.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

// Pide al sistema la ubicación actual, una sola vez. Devuelve null si no se pudo obtener
@SuppressLint("MissingPermission") // el permiso se revisa antes de llamar a esta función
private fun obtenerUbicacionActual(context: Context, onResultado: (Location?) -> Unit) {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val tienePrecisa = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Con permiso preciso se puede usar el GPS, y con permiso aproximado solo la red
    val proveedores = if (tienePrecisa) {
        listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
    } else {
        listOf(LocationManager.NETWORK_PROVIDER)
    }
    val proveedor = proveedores.firstOrNull { manager.isProviderEnabled(it) }
    if (proveedor == null) {
        onResultado(null) // ubicación del teléfono apagada
        return
    }

    try {
        LocationManagerCompat.getCurrentLocation(
            manager, proveedor, null, ContextCompat.getMainExecutor(context)
        ) { ubicacion -> onResultado(ubicacion) }
    } catch (e: SecurityException) {
        onResultado(null)
    }
}

// Ubicación de la oportunidad: botón para obtener las coordenadas y mostrarlas
@Composable
fun SeccionUbicacion(
    latitud: Double?,
    longitud: Double?,
    onUbicacionObtenida: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var buscando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    fun buscar() {
        buscando = true
        mensaje = null
        obtenerUbicacionActual(context) { ubicacion ->
            buscando = false
            if (ubicacion != null) {
                onUbicacionObtenida(ubicacion.latitude, ubicacion.longitude)
            } else {
                mensaje = "No se pudo obtener la ubicación. Revisa que el GPS esté activado"
            }
        }
    }

    // Lanzador del permiso de ubicación (se piden los dos y basta con que se acepte uno)
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) buscar()
        else mensaje = "Se necesita el permiso de ubicación para registrar el lugar"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Las coordenadas aparecen con una animación al obtenerlas
        AnimatedVisibility(
            visible = latitud != null && longitud != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text(
                    "Lat: %.5f   Lon: %.5f".format(latitud ?: 0.0, longitud ?: 0.0),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        OutlinedButton(
            enabled = !buscando,
            onClick = {
                val tienePermiso = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).any {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }
                if (tienePermiso) buscar()
                else lanzadorPermiso.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        ) {
            if (buscando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Text("Buscando ubicación...", modifier = Modifier.padding(start = 8.dp))
            } else {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Text(
                    if (latitud == null) "Registrar ubicación" else "Actualizar ubicación",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        AnimatedVisibility(visible = mensaje != null) {
            Text(mensaje ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}
