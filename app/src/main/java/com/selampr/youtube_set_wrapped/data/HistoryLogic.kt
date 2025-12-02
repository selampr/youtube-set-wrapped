package com.selampr.youtube_set_wrapped.data

import android.util.Log
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WatchEntry(
    val title: String,
    val url: String,
    val time: String?,
    val channel: String?
)

data class VideoStat(
    val title: String,
    val count: Int
)

private val KEYWORDS = listOf(
    "live set", "dj set", "boiler room", "tiny desk", "festival",
    "concert", "live session", "session", "mix", "b2b",
    "closing set", "opening set"
)

private const val TARGET_YEAR = 2025

fun parseHistoryHtml(html: String): List<WatchEntry> {
    Log.d("Stats", "parseHistoryHtml: iniciando parseo con parada anticipada…")

    val doc = Jsoup.parse(html)
    val items = doc.select("div.outer-cell, div.content-cell")

    Log.d("Stats", "parseHistoryHtml: elementos detectados = ${items.size}")

    val list = mutableListOf<WatchEntry>()

    for ((i, item) in items.withIndex()) {

        val link = item.selectFirst("a") ?: continue
        val title = link.text()
        val url = link.attr("href")

        // Filtrar que sea vídeo
        if (!url.contains("watch") && !url.contains("youtu.be")) {
            continue
        }

        val channel = item.select("a").getOrNull(1)?.text()
        val rawDate = item.ownText().trim().ifBlank { null }

        // Intentamos extraer año
        val date = parseDate(rawDate)

        if (date != null) {
            val year = date.year

            // ENCUENTRO DE CONTROL → si ya hemos pasado a 2024 o menos, paramos
            if (year < 2025) {
                Log.d("Stats", "parseHistoryHtml: encontrado año $year (< 2025). PARANDO parseo aquí")
                break
            }
        } else {
            Log.d("Stats", "parseHistoryHtml: no se pudo parsear fecha para entrada $i, continúo")
        }

        list += WatchEntry(
            title = title,
            url = url,
            time = rawDate,
            channel = channel
        )

        Log.d("Stats", "parseHistoryHtml: entrada $i añadida (title='$title')")
    }

    Log.d("Stats", "parseHistoryHtml: total entradas parseadas = ${list.size}")
    return list
}

private fun parseDate(dateText: String?): LocalDate? {
    if (dateText == null) {
        Log.d("Stats", "parseDate: fecha nula, se ignora")
        return null
    }

    // Ejemplos de dateText:
    // "Has visto 2 dic 2025, 10:28:02 CET"
    // "Has visto Visto a las 9:49 2 dic 2025, 9:49:27 CET"
    // Queremos extraer solo "2 dic 2025"

    val regex = Regex("""(\d{1,2}\s+[A-Za-zñáéíóúÁÉÍÓÚ]{3,}\s+\d{4})""")
    val match = regex.find(dateText)

    if (match == null) {
        Log.d("Stats", "parseDate: no se ha encontrado fecha en '$dateText'")
        return null
    }

    val cleanDate = match.value.trim()
    Log.d("Stats", "parseDate: fecha extraída = '$cleanDate'")

    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "ES"))

    return try {
        val date = LocalDate.parse(cleanDate, formatter)
        Log.d("Stats", "parseDate: fecha parseada correctamente → $date")
        date
    } catch (e: Exception) {
        Log.d("Stats", "parseDate: ERROR parseando '$cleanDate' → ${e.message}")
        null
    }
}

private fun isFrom2025(e: WatchEntry): Boolean {
    val year = parseDate(e.time)?.year
    val result = year == TARGET_YEAR
    Log.d("Stats", "isFrom2025: '${e.title}' → year=$year match=$result")
    return result
}

private fun isMusicSet(title: String): Boolean {
    val t = title.lowercase()
    val match = KEYWORDS.any { t.contains(it) }
    Log.d("Stats", "isMusicSet: title='$title' match=$match")
    return match
}

fun computeStatsFor2025(entries: List<WatchEntry>): List<VideoStat> {
    Log.d("Stats", "computeStatsFor2025: entradas recibidas = ${entries.size}")

    val fromYear = entries.filter { isFrom2025(it) }
    Log.d("Stats", "computeStatsFor2025: entradas del año 2025 = ${fromYear.size}")

    val sets = fromYear.filter { isMusicSet(it.title) }
    Log.d("Stats", "computeStatsFor2025: sets detectados = ${sets.size}")

    if (sets.isEmpty()) {
        Log.d("Stats", "computeStatsFor2025: no se han encontrado sets en 2025")
    }

    val stats = sets
        .groupBy { it.title }
        .map { (title, list) -> VideoStat(title, list.size) }
        .sortedByDescending { it.count }

    Log.d("Stats", "computeStatsFor2025: estadísticas generadas = ${stats.size}")
    return stats
}
