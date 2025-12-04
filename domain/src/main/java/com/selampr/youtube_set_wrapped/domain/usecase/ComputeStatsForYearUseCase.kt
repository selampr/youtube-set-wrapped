package com.selampr.youtube_set_wrapped.domain.usecase

import com.selampr.youtube_set_wrapped.domain.model.VideoStat
import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class ComputeStatsForYearUseCase @Inject constructor() {

    operator fun invoke(
        entries: List<WatchEntry>,
        targetYear: Int = DEFAULT_TARGET_YEAR
    ): List<VideoStat> {
        val filtered = entries.mapNotNull { entry ->
            val date = parseDate(entry.time) ?: return@mapNotNull null

            if (date.year != targetYear) return@mapNotNull null
            if (!isMusicSet(entry.title)) return@mapNotNull null
            if (isAdEntry(entry)) return@mapNotNull null

            entry to date
        }

        if (filtered.isEmpty()) return emptyList()

        val groupedByTitle = filtered.groupBy { it.first.title }

        return groupedByTitle.map { (title, list) ->
            val dates = list.map { it.second }.sorted()

            var clusters = 0
            var lastClusterDate: LocalDate? = null

            for (date in dates) {
                if (lastClusterDate == null) {
                    clusters++
                    lastClusterDate = date
                    continue
                }

                val diff = ChronoUnit.DAYS.between(lastClusterDate, date)
                if (diff > 3) {
                    clusters++
                    lastClusterDate = date
                }
            }

            VideoStat(title = title, count = clusters)
        }
            .filter { it.count > 0 }
            .sortedByDescending { it.count }
    }

    private fun parseDate(dateText: String?): LocalDate? {
        if (dateText == null) return null

        val regex = Regex("""(\d{1,2}\s+[A-Za-zñáéíóúÁÉÍÓÚ]{3,}\s+\d{4})""")
        val match = regex.find(dateText) ?: return null
        val cleanDate = match.value.trim()

        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es", "ES"))

        return try {
            LocalDate.parse(cleanDate, formatter)
        } catch (_: Exception) {
            null
        }
    }

    private fun isAdEntry(entry: WatchEntry): Boolean {
        val timeText = entry.time?.lowercase().orEmpty()
        val titleText = entry.title.lowercase()

        if (timeText.contains("anuncios de google")) return true

        return titleText.contains("youtube es") &&
            timeText.contains("anuncios") &&
            titleText.contains("gallery session")
    }

    private fun isMusicSet(title: String): Boolean {
        val normalized = title.lowercase()
        return KEYWORDS.any { normalized.contains(it) }
    }

    companion object {
        const val DEFAULT_TARGET_YEAR = 2025

        private val KEYWORDS = listOf(
            "live set", "dj set", "boiler room", "tiny desk", "festival",
            "concert", "live session", "session", "mix", "b2b",
            "closing set", "opening set"
        )
    }
}
