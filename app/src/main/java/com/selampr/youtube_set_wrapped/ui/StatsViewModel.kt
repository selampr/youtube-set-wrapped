package com.selampr.youtube_set_wrapped.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selampr.youtube_set_wrapped.data.ai.OpenAiStatsService
import com.selampr.youtube_set_wrapped.data.remote.model.VideoResultDto
import com.selampr.youtube_set_wrapped.domain.model.VideoStat
import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import com.selampr.youtube_set_wrapped.domain.usecase.ComputeStatsForYearUseCase
import com.selampr.youtube_set_wrapped.domain.usecase.ParseHistoryFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val parseHistoryFile: ParseHistoryFileUseCase,
    private val computeStatsForYear: ComputeStatsForYearUseCase,
    private val openAiStatsService: OpenAiStatsService
) : ViewModel() {

    private val allEntries = mutableListOf<WatchEntry>()

    var stats by mutableStateOf<List<VideoStat>>(emptyList())
        private set

    var loadedEntriesCount by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var aiSummary by mutableStateOf<String?>(null)
        private set

    var aiVideos by mutableStateOf<List<VideoResultDto>>(emptyList())
        private set

    var totalDurationMinutes by mutableStateOf<Long?>(null)
        private set

    var isAiLoading by mutableStateOf(false)
        private set

    var aiError by mutableStateOf<String?>(null)
        private set


    fun reset() {
        allEntries.clear()
        stats = emptyList()
        loadedEntriesCount = 0
        aiSummary = null
        aiVideos = emptyList()
        totalDurationMinutes = null
        aiError = null
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
    fun loadFilesAsync(context: Context, uris: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            reset()

            val htmlList = uris.mapNotNull { uri ->
                runCatching {
                    context.contentResolver.openInputStream(uri)
                        ?.bufferedReader()
                        ?.readText()
                }.getOrNull()
            }

            addHtmlFiles(htmlList)
            isLoading = false
        }
    }

    fun generateAiStats(): Job = viewModelScope.launch(Dispatchers.IO) {
        if (isAiLoading) return@launch
        isAiLoading = true
        aiError = null

        // Ensure stats exist before asking OpenAI for a summary.
        if (stats.isEmpty()) {
            generateStats()
        }

        if (!openAiStatsService.isConfigured()) {
            aiError = "OpenAI API key is missing. Add OPENAI_API_KEY to local.properties."
            isAiLoading = false
            return@launch
        }

        if (stats.isEmpty()) {
            aiError = "No stats available to summarize."
            isAiLoading = false
            return@launch
        }

        val result = runCatching { openAiStatsService.summarize(stats) }
        result.onSuccess { summary ->
            aiSummary = summary
            totalDurationMinutes = null
            aiVideos = emptyList()
        }.onFailure { throwable ->
            aiError = throwable.message ?: "Unknown error"
        }

        isAiLoading = false
    }


}
