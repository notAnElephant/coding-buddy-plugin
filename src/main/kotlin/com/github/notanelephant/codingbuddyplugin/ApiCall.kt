package com.github.notanelephant.codingbuddyplugin

import com.github.notanelephant.codingbuddyplugin.exceptions.PromptTooLongException
import com.github.notanelephant.codingbuddyplugin.wrapper.GPT3Model
import com.github.notanelephant.codingbuddyplugin.wrapper.HttpTimeout
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClientConfig
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import kotlin.time.Duration.Companion.seconds

object ApiCall {
    private val model = GPT3Model.DAVINCI
    private const val TOKENTOCHAR = 4  //it is a rule of thumb that 1 token is 4 characters

    suspend fun getApiResponse(apiKey: String, prompt: String, code: String = ""): String {

        val openAI = OpenAIClient(
            OpenAIClientConfig(
                apiKey,
                HttpTimeout(request = 60.seconds),
            )
        )
        val textToSend = "$prompt:\n$code"
        if (textToSend.length > model.maxTokens * TOKENTOCHAR) {
            throw PromptTooLongException("The text you attempted to send is too long", model.maxTokens * TOKENTOCHAR)
        }
        val createCompletionResponse =
            openAI.createCompletion(
                CreateCompletionRequest(
                    model = model.name,
                    prompt = textToSend,
                    maxTokens = 3000,
                    temperature = 0.0,
                ),
            )

        return createCompletionResponse.choices.joinToString("\n") { it.text }.trim()
    }

    fun getApiKey(): String {
        return System.getenv("OPENAI_API_KEY")
            .let {
                if (it.isNullOrEmpty()) {
                    throw Exception("No API key found")
                } else {
                    it
                }
            }
    }
    fun getApiKey2(): String = "".let { keyFromSettings -> //AppSettingsState.instance.apiKey
        keyFromSettings.ifEmpty {
            System.getenv("OPENAI_API_KEY")
                .let {
                    if (it.isNullOrEmpty()) {
                        throw Exception("No API key found")
                    } else {
                        it
                    }
                }
        }
    }
}