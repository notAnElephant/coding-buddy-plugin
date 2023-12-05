package com.github.notanelephant.codingbuddyplugin

import com.github.notanelephant.codingbuddyplugin.exceptions.NoApiKeyException
import com.github.notanelephant.codingbuddyplugin.settings.AppSettingsState
import com.github.notanelephant.codingbuddyplugin.wrapper.GPT3Model
import com.github.notanelephant.codingbuddyplugin.wrapper.HttpTimeout
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClientConfig
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import kotlin.time.Duration.Companion.seconds

object ApiCall {
    private val model = GPT3Model.DAVINCI
    suspend fun getApiResponse(apiKey: String, prompt: String, code: String = ""): String {
        val openAI = OpenAIClient(OpenAIClientConfig(
            apiKey,
            HttpTimeout(request = 60.seconds),
        ))
        val textToSend = "$prompt:\n$code"
        if(textToSend.length > model.maxTokens * 4) { //TODO *4 stuff from git
            //ErrorDialog.show(currentProject, "The code is too long") //TODO proper error throwing from git
            return "ERROR: The code is too long"
        }
        val createCompletionResponse =
            openAI.createCompletion(
                CreateCompletionRequest(
                    model = model.modelName,
                    prompt = textToSend,
                    maxTokens = 3000,
                    temperature = 0.0,
                ),
            )

        return createCompletionResponse.choices.joinToString("\n") { it.text }.trim()
    }

    fun getApiKey(): String = AppSettingsState.instance.apiKey.let { keyFromSettings ->
        keyFromSettings.ifEmpty {
            System.getenv("OPENAI_API_KEY")
                .let {
                    if (it.isNullOrEmpty()) {
                        throw NoApiKeyException("No API key found", "Please set your API key in the settings")
                    } else {
                        it
                    }
                }
        }
    }
}
