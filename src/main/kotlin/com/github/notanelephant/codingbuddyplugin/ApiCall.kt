package com.github.notanelephant.codingbuddyplugin

import com.github.notanelephant.codingbuddyplugin.wrapper.GPT3Model
import com.github.notanelephant.codingbuddyplugin.wrapper.HttpTimeout
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClientConfig
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import kotlin.time.Duration.Companion.seconds

object ApiCall {
        private val apiKey = requireNotNull(System.getenv("OPENAI_API_KEY")) {
            //ErrorDialog.show(currentProject, "OpenAI API key is not present or incorrect") //TODO
            "ERROR: OPENAI_API_KEY env variable not set"
        }
        private val config =
            OpenAIClientConfig(
                apiKey,
                HttpTimeout(request = 60.seconds),
            )
        private val openAI = OpenAIClient(config)
        suspend fun getApiResponse(prompt: String, code: String = ""): String {
            val model = GPT3Model.DAVINCI.modelName
            val textToSend = "$prompt:\n$code"
            val createCompletionResponse =
                openAI.createCompletion(
                    CreateCompletionRequest(
                        model = model,
                        prompt = textToSend,
                        maxTokens = 1000,
                        temperature = 0.7,
                    ),
                )

            return createCompletionResponse.choices.joinToString("\n") { it.text }.trim()
        }
}