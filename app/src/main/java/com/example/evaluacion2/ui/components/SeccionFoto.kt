package com.example.evaluacion2.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

// Crea un archivo vacío donde la cámara va a guardar la foto.
// Va en filesDir (y no en cacheDir) para que la foto siga existiendo cuando se vuelva a abrir la oportunidad
private fun crearUriFoto(context: Context): Uri {
    val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
    val archivo = File.createTempFile("foto_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

// Foto de evidencia: muestra la foto y el botón para abrir la cámara
@Composable
fun SeccionFoto(uri: Uri?, onFotoCapturada: (Uri?) -> Unit) {
    val context = LocalContext.current

    // Dirección donde la cámara deja la foto mientras se toma
    var uriTemporal by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    // Lanzador de la cámara: devuelve true si se tomó la foto, y false si se canceló
    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { tomada ->
        if (tomada) onFotoCapturada(uriTemporal)
    }

    fun abrirCamara() {
        val nuevaUri = crearUriFoto(context)
        uriTemporal = nuevaUri
        lanzadorCamara.launch(nuevaUri)
    }

    // Lanzador del permiso de cámara
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mensaje = null
            abrirCamara()
        } else {
            mensaje = "Se necesita el permiso de cámara para tomar la foto"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ImagenInteligente(
            uri = uri,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        OutlinedButton(
            onClick = {
                // Si ya tiene permiso abre la cámara, y si no, lo pide
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) abrirCamara()
                else lanzadorPermiso.launch(Manifest.permission.CAMERA)
            }
        ) {
            Text(if (uri == null) "Tomar foto de evidencia" else "Tomar otra foto")
        }

        mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}