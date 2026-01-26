package com.selampr.youtube_set_wrapped.data.ai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.selampr.youtube_set_wrapped.domain.model.VideoStat

class OpenAiStatsService(
    private val apiKey: String
) {

    fun isConfigured(): Boolean = apiKey.isNotBlank()

    suspend fun summarize(stats: List<VideoStat>): String {
        require(isConfigured()) { "OpenAI API key is missing." }

        if (stats.isEmpty()) {
            return "No stats available to summarize."
        }

        val topStats = stats.sortedByDescending { it.count }.take(15)
        val statsLines = topStats.joinToString(separator = "\n") { "- ${it.title} (${it.count})" }

        val prompt = buildString {
            appendLine("Summarize this user's YouTube viewing habits based on the list below.")
            appendLine("Keep it concise: 2-4 sentences plus 3 short bullet highlights.")
            appendLine()
            appendLine("Video stats (title -> watch count):")
            appendLine(statsLines)
        }

        val completion = openAI().chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4o-mini"),
                messages = listOf(
                    ChatMessage.System(
                        "You are a helpful assistant that summarizes YouTube viewing statistics."
                    ),
                    ChatMessage.User(prompt)
                ),
                temperature = 0.4
            )
        )

        return completion.choices.firstOrNull()?.message?.content?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "No summary returned."
    }

    suspend fun generateVibe(stats: List<VideoStat>): String {
        require(isConfigured()) { "OpenAI API key is missing." }

        if (stats.isEmpty()) {
            return "No stats available to summarize."
        }

        val topStats = stats.sortedByDescending { it.count }.take(15)
        val statsLines = topStats.joinToString(separator = "\n") { "- ${it.title} (${it.count})" }

        val prompt = buildString {
            appendLine("Write ONE short sentence in English about the user's year vibe.")
            appendLine("Do not mention genres, artists, or numbers. Max 12 words.")
            appendLine("It should sound natural and capture the mood.")
            appendLine()
            appendLine("Video stats (title -> watch count):")
            appendLine(statsLines)
        }

        val completion = openAI().chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4o-mini"),
                messages = listOf(
                    ChatMessage.System(
                        "You are a helpful assistant that summarizes YouTube stats in one short sentence."
                    ),
                    ChatMessage.User(prompt)
                ),
                temperature = 0.6
            )
        )

        return completion.choices.firstOrNull()?.message?.content?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "No summary returned."
    }

    suspend fun generateVideoLine(title: String, channel: String?): String {
        require(isConfigured()) { "OpenAI API key is missing." }

        val prompt = buildString {
            appendLine("Write ONE short sentence in English about this video's vibe.")
            appendLine("Base it on the title and channel. Max 12 words.")
            appendLine()
            appendLine("Title: $title")
            appendLine("Channel: ${channel ?: "Unknown"}")
        }

        val completion = openAI().chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4o-mini"),
                messages = listOf(
                    ChatMessage.System(
                        "You write short, punchy one-liners about YouTube videos."
                    ),
                    ChatMessage.User(prompt)
                ),
                temperature = 0.6
            )
        )

        return completion.choices.firstOrNull()?.message?.content?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "No summary returned."
    }

    private fun openAI(): OpenAI = OpenAI(token = apiKey)
}
