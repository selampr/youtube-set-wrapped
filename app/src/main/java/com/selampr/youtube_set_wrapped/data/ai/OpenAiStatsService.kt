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

    private fun openAI(): OpenAI = OpenAI(token = apiKey)
}
