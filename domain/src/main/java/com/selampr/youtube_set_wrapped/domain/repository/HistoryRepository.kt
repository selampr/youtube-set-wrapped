package com.selampr.youtube_set_wrapped.domain.repository

import com.selampr.youtube_set_wrapped.domain.model.WatchEntry

interface HistoryRepository {
    fun parseHistoryHtml(html: String): List<WatchEntry>
}
