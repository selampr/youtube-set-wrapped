package com.selampr.youtube_set_wrapped.data.repository

import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import com.selampr.youtube_set_wrapped.domain.repository.HistoryRepository
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor() : HistoryRepository {

    override fun parseHistoryHtml(html: String): List<WatchEntry> {
        val doc = Jsoup.parse(html)
        val items = doc.select("div.outer-cell, div.content-cell")

        return items.mapNotNull { item ->
            val link = item.selectFirst("a") ?: return@mapNotNull null
            val title = link.text()
            val url = link.attr("href")

            if (!url.contains("watch") && !url.contains("youtu.be")) {
                return@mapNotNull null
            }

            val channel = item.select("a").getOrNull(1)?.text()
            val rawDate = item.ownText().trim().ifBlank { null }

            WatchEntry(
                title = title,
                url = url,
                time = rawDate,
                channel = channel
            )
        }
    }
}
