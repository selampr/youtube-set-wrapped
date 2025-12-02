package com.selampr.youtube_set_wrapped.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.selampr.youtube_set_wrapped.data.VideoStat
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun StatsScreen(vm: StatsViewModel = viewModel()) {
    val context = LocalContext.current
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickFiles = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { result ->
        Log.d("Stats", "StatsScreen: archivos seleccionados = ${result.size}")
        uris = result
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = {
            Log.d("Stats", "StatsScreen: abriendo selector de archivos HTML")
            pickFiles.launch(arrayOf("text/html"))
        }) {
            Text("Seleccionar archivos HTML")
        }

        Spacer(Modifier.height(8.dp))
        Text("Archivos seleccionados: ${uris.size}")

        Spacer(Modifier.height(8.dp))

        Button(
            enabled = uris.isNotEmpty(),
            onClick = {
                Log.d("Stats", "StatsScreen: iniciando carga de archivos")
                vm.reset()

                val htmlList =
                    uris.mapNotNull { uri ->
                        runCatching {
                            Log.d("Stats", "StatsScreen: leyendo archivo → $uri")
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                BufferedReader(InputStreamReader(input)).readText()
                            }
                        }.onFailure {
                            Log.d("Stats", "StatsScreen: ERROR leyendo archivo $uri → ${it.message}")
                        }.getOrNull()
                    }

                Log.d("Stats", "StatsScreen: archivos leídos correctamente = ${htmlList.size}")

                vm.addHtmlFiles(htmlList)
            }
        ) {
            Text("Cargar datos")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            enabled = vm.loadedEntriesCount > 0,
            onClick = {
                Log.d("Stats", "StatsScreen: generando estadísticas…")
                vm.generateStats()
            }
        ) {
            Text("Generar estadísticas (2025)")
        }

        Spacer(Modifier.height(16.dp))

        StatsResult(vm.stats)
    }
}

@Composable
fun StatsResult(stats: List<VideoStat>) {
    if (stats.isEmpty()) {
        Log.d("Stats", "StatsResult: no hay estadísticas disponibles")
        Text("No hay estadísticas todavía o no hay sets del 2025.")
        return
    }

    Log.d("Stats", "StatsResult: mostrando ${stats.size} resultados")

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        stats.forEach { s ->
            Text(
                "${s.title}\nReproducciones: ${s.count}\n",
                Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
