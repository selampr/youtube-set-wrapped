package com.selampr.youtube_set_wrapped.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.selampr.youtube_set_wrapped.data.VideoStat
import com.selampr.youtube_set_wrapped.data.WatchEntry
import com.selampr.youtube_set_wrapped.data.computeStatsFor2025
import com.selampr.youtube_set_wrapped.data.parseHistoryHtml

class StatsViewModel : ViewModel() {

    private val allEntries = mutableListOf<WatchEntry>()

    var stats by mutableStateOf<List<VideoStat>>(emptyList())
        private set

    var loadedEntriesCount by mutableStateOf(0)
        private set

    fun reset() {
        Log.d("Stats", "ViewModel.reset: limpiando datos de memoria")
        allEntries.clear()
        stats = emptyList()
        loadedEntriesCount = 0
    }

    fun addHtmlFiles(htmlFiles: List<String>) {
        Log.d("Stats", "ViewModel.addHtmlFiles: archivos recibidos = ${htmlFiles.size}")

        htmlFiles.forEachIndexed { i, html ->
            Log.d("Stats", "ViewModel.addHtmlFiles: procesando archivo $i")
            val entries = parseHistoryHtml(html)
            Log.d("Stats", "ViewModel.addHtmlFiles: archivo $i → ${entries.size} entradas")
            allEntries += entries
        }

        loadedEntriesCount = allEntries.size
        Log.d("Stats", "ViewModel.addHtmlFiles: entradas acumuladas = $loadedEntriesCount")
    }

    fun generateStats() {
        Log.d("Stats", "ViewModel.generateStats: generando estadísticas…")
        stats = computeStatsFor2025(allEntries)
        Log.d("Stats", "ViewModel.generateStats: estadísticas finales = ${stats.size}")
    }
}
