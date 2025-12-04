package com.selampr.youtube_set_wrapped.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.selampr.youtube_set_wrapped.domain.model.VideoStat
import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import com.selampr.youtube_set_wrapped.domain.usecase.ComputeStatsForYearUseCase
import com.selampr.youtube_set_wrapped.domain.usecase.ParseHistoryFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val parseHistoryFile: ParseHistoryFileUseCase,
    private val computeStatsForYear: ComputeStatsForYearUseCase
) : ViewModel() {

    private val allEntries = mutableListOf<WatchEntry>()

    var stats by mutableStateOf<List<VideoStat>>(emptyList())
        private set

    var loadedEntriesCount by mutableStateOf(0)
        private set

    fun reset() {
        allEntries.clear()
        stats = emptyList()
        loadedEntriesCount = 0
    }

    fun addHtmlFiles(htmlFiles: List<String>) {
        htmlFiles.forEach { html ->
            val entries = parseHistoryFile(html)
            allEntries += entries
        }

        loadedEntriesCount = allEntries.size
    }

    fun generateStats(targetYear: Int = ComputeStatsForYearUseCase.DEFAULT_TARGET_YEAR) {
        stats = computeStatsForYear(allEntries, targetYear)
    }
}
