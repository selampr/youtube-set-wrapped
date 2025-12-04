package com.selampr.youtube_set_wrapped.domain.usecase

import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import com.selampr.youtube_set_wrapped.domain.repository.HistoryRepository
import javax.inject.Inject

class ParseHistoryFileUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    operator fun invoke(html: String): List<WatchEntry> = repository.parseHistoryHtml(html)
}
