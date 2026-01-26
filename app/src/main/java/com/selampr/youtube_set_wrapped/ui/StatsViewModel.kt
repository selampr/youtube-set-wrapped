package com.selampr.youtube_set_wrapped.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selampr.youtube_set_wrapped.data.ai.OpenAiStatsService
import com.selampr.youtube_set_wrapped.data.remote.YoutubeDataService
import com.selampr.youtube_set_wrapped.data.remote.model.VideoResultDto
import com.selampr.youtube_set_wrapped.domain.model.VideoStat
import com.selampr.youtube_set_wrapped.domain.model.WatchEntry
import com.selampr.youtube_set_wrapped.domain.usecase.ComputeStatsForYearUseCase
import com.selampr.youtube_set_wrapped.domain.usecase.ParseHistoryFileUseCase
import com.selampr.youtube_set_wrapped.util.YoutubeThumbnail
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
    private val openAiStatsService: OpenAiStatsService,
    private val youtubeDataService: YoutubeDataService
) : ViewModel() {

    companion object {
        private const val TAG = "StatsViewModel"
    }
    private val allEntries = mutableListOf<WatchEntry>()

    var stats by mutableStateOf<List<VideoStat>>(emptyList())
        private set

    var loadedEntriesCount by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var aiSummary by mutableStateOf<String?>(null)
        private set

    var aiVibe by mutableStateOf<String?>(null)
        private set

    var aiVideos by mutableStateOf<List<VideoResultDto>>(emptyList())
        private set

    var totalDurationMinutes by mutableStateOf<Long?>(null)
        private set

    var isAiLoading by mutableStateOf(false)
        private set

    var isAiVibeLoading by mutableStateOf(false)
        private set

    var isDurationLoading by mutableStateOf(false)
        private set

    var isTopVideoLoading by mutableStateOf(false)
        private set

    var topVideoTitle by mutableStateOf<String?>(null)
        private set

    var topVideoArtist by mutableStateOf<String?>(null)
        private set

    var topVideoMinutes by mutableStateOf<Long?>(null)
        private set

    var topVideoThumbnailUrl by mutableStateOf<String?>(null)
        private set

    var topVideoLine by mutableStateOf<String?>(null)
        private set

    var topVideoError by mutableStateOf<String?>(null)
        private set

    var aiError by mutableStateOf<String?>(null)
        private set


    fun reset() {
        allEntries.clear()
        stats = emptyList()
        loadedEntriesCount = 0
        aiSummary = null
        aiVibe = null
        aiVideos = emptyList()
        totalDurationMinutes = null
        isAiLoading = false
        isAiVibeLoading = false
        isDurationLoading = false
        isTopVideoLoading = false
        topVideoTitle = null
        topVideoArtist = null
        topVideoMinutes = null
        topVideoThumbnailUrl = null
        topVideoLine = null
        topVideoError = null
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

    fun generateAiVibe(): Job = viewModelScope.launch(Dispatchers.IO) {
        if (isAiVibeLoading) return@launch
        isAiVibeLoading = true
        aiError = null
        aiVibe = null

        if (stats.isEmpty()) {
            generateStats()
        }

        if (!openAiStatsService.isConfigured()) {
            aiError = "OpenAI API key is missing. Add OPENAI_API_KEY to local.properties."
            isAiVibeLoading = false
            return@launch
        }

        if (stats.isEmpty()) {
            aiError = "No stats available to summarize."
            isAiVibeLoading = false
            return@launch
        }

        val result = runCatching { openAiStatsService.generateVibe(stats) }
        result.onSuccess { vibe ->
            aiVibe = vibe
        }.onFailure { throwable ->
            aiError = throwable.message ?: "Unknown error"
        }

        isAiVibeLoading = false
    }

    fun generateTotalDurationMinutes(): Job = viewModelScope.launch(Dispatchers.IO) {
        if (isDurationLoading) return@launch
        isDurationLoading = true
        aiError = null
        totalDurationMinutes = null

        if (stats.isEmpty()) {
            generateStats()
        }

        if (!youtubeDataService.isConfigured()) {
            aiError = "YouTube API key is missing. Add YOUTUBE_API_KEY to local.properties."
            isDurationLoading = false
            return@launch
        }

        if (stats.isEmpty()) {
            aiError = "No stats available to summarize."
            isDurationLoading = false
            return@launch
        }

        val titleToVideoId = buildTitleToVideoId()
        val countByVideoId = stats.mapNotNull { stat ->
            titleToVideoId[stat.title]?.let { id -> id to stat.count }
        }

        if (countByVideoId.isEmpty()) {
            aiError = "No video IDs found to fetch durations."
            isDurationLoading = false
            return@launch
        }

        val durations = runCatching {
            youtubeDataService.fetchDurationsSeconds(countByVideoId.map { it.first })
        }

        durations.onSuccess { durationsById ->
            val totalSeconds = countByVideoId.sumOf { (id, count) ->
                (durationsById[id] ?: 0L) * count.toLong()
            }
            totalDurationMinutes = totalSeconds / 60L
        }.onFailure { throwable ->
            aiError = throwable.message ?: "Unknown error"
        }

        isDurationLoading = false
    }

    fun loadTopVideo(): Job = viewModelScope.launch(Dispatchers.IO) {
        if (isTopVideoLoading) return@launch
        isTopVideoLoading = true
        topVideoError = null
        topVideoTitle = null
        topVideoArtist = null
        topVideoMinutes = null
        topVideoThumbnailUrl = null
        topVideoLine = null

        if (stats.isEmpty()) {
            generateStats()
        }

        val topUrl = getTopVideoUrl()
        if (topUrl.isNullOrBlank()) {
            topVideoError = "No top video found. Upload stats first."
            isTopVideoLoading = false
            return@launch
        }

        val videoId = YoutubeThumbnail.extractVideoId(topUrl)
        if (videoId == null) {
            topVideoError = "Could not parse video ID."
            isTopVideoLoading = false
            return@launch
        }

        if (!youtubeDataService.isConfigured()) {
            topVideoError = "YouTube API key is missing. Add YOUTUBE_API_KEY to local.properties."
            isTopVideoLoading = false
            return@launch
        }

        val detailsResult = runCatching { youtubeDataService.fetchVideoDetails(videoId) }
        detailsResult.onSuccess { details ->
            if (details == null) {
                topVideoError = "No video details returned."
                return@onSuccess
            }

            topVideoTitle = details.title
            topVideoArtist = details.channelTitle
            topVideoMinutes = details.durationSeconds / 60L
            topVideoThumbnailUrl = YoutubeThumbnail.thumbnailUrl(topUrl)

            if (openAiStatsService.isConfigured()) {
                val lineResult = runCatching {
                    openAiStatsService.generateVideoLine(details.title, details.channelTitle)
                }

                lineResult.onSuccess { line ->
                    topVideoLine = line
                }.onFailure { throwable ->
                    Log.e(TAG, "AI line error", throwable)
                }
            }
        }.onFailure { throwable ->
            Log.e(TAG, "YouTube details error", throwable)
            topVideoError = throwable.message ?: throwable.toString()
        }

        isTopVideoLoading = false
    }

    fun getThumbnailUrl(videoUrl: String): String? {
        return YoutubeThumbnail.thumbnailUrl(videoUrl)
    }

    fun getTopVideoThumbnailUrlFromStats(): String? {
        val url = getTopVideoUrl() ?: return null
        return getThumbnailUrl(url)
    }

    fun getTopVideoUrl(): String? {
        val topTitle = stats.maxByOrNull { it.count }?.title ?: return null
        return buildTitleToUrl()[topTitle]
    }

    fun isOpenAiConfigured(): Boolean = openAiStatsService.isConfigured()

    private fun buildTitleToVideoId(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        allEntries.forEach { entry ->
            if (map.containsKey(entry.title)) return@forEach
            val videoId = YoutubeThumbnail.extractVideoId(entry.url) ?: return@forEach
            map[entry.title] = videoId
        }
        return map
    }

    private fun buildTitleToUrl(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        allEntries.forEach { entry ->
            if (map.containsKey(entry.title)) return@forEach
            map[entry.title] = entry.url
        }
        return map
    }
}
