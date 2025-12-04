package com.selampr.youtube_set_wrapped.data

import android.util.Log
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.temporal.ChronoUnit


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

            // si ya hemos pasado a 2024 paramos
            if (year < 2025) {
                Log.d("Stats", "parseHistoryHtml: encontrado año $year (< 2025). PARANDO parseo")
                break
            }
        } else {
            Log.d("Stats", "parseHistoryHtml: no se pudo parsear fecha para entrada $i, continuo")
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

private fun isAdEntry(entry: WatchEntry): Boolean {
    val timeText = entry.time?.lowercase() ?: ""
    val titleText = entry.title.lowercase()

    if (timeText.contains("anuncios de google")) {
        Log.d("Stats", "isAdEntry: detectado anuncio por time='${entry.time}'")
        return true
    }

    // Puedes añadir más heurísticas si quieres
    if (titleText.contains("youtube es") && timeText.contains("anuncios") && titleText.contains("GALLERY SESSION")) {
        Log.d("Stats", "isAdEntry: detectado anuncio por combinación título/time")
        return true
    }

    return false
}

private fun parseDate(dateText: String?): LocalDate? {
    if (dateText == null) {
        Log.d("Stats", "parseDate: fecha nula, se ignora")
        return null
    }

    // Buscar "2 dic 2025" dentro del texto
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

    // 1) Filtrar y mapear a (entry, fecha)
    val filtered = entries.mapNotNull { e ->
        val date = parseDate(e.time)
        if (date == null) return@mapNotNull null

        if (date.year != 2025) {
            return@mapNotNull null
        }

        if (!isMusicSet(e.title)) {
            return@mapNotNull null
        }

        if (isAdEntry(e)) {
            return@mapNotNull null
        }

        e to date
    }

    Log.d("Stats", "computeStatsFor2025: tras filtros año/set/no anuncio = ${filtered.size}")

    if (filtered.isEmpty()) {
        Log.d("Stats", "computeStatsFor2025: no hay entradas válidas tras filtrado")
        return emptyList()
    }

    // 2) Agrupar por título (mezcla todas las cuentas)
    val groupedByTitle = filtered.groupBy { it.first.title }

    val stats = groupedByTitle.map { (title, list) ->
        // Solo nos quedamos con las fechas
        val dates = list.map { it.second }.sorted()

        // 3) Contar "sesiones" separadas > 3 días (ventana de 3 días)
        var clusters = 0
        var lastClusterDate: LocalDate? = null

        for (d in dates) {
            if (lastClusterDate == null) {
                clusters++
                lastClusterDate = d
                continue
            }

            val diff = ChronoUnit.DAYS.between(lastClusterDate, d)
            if (diff > 3) {
                // Han pasado más de 3 días → nuevo "bloque"
                clusters++
                lastClusterDate = d
            } else {
                // Está dentro de la ventana de 3 días → mismo bloque, no suma
            }
        }

        Log.d("Stats", "computeStatsFor2025: '$title' → fechas=${dates.size}, clusters=$clusters")

        VideoStat(title = title, count = clusters)
    }
        .filter { it.count > 0 }
        .sortedByDescending { it.count }

    Log.d("Stats", "computeStatsFor2025: estadísticas generadas = ${stats.size}")
    return stats
}
