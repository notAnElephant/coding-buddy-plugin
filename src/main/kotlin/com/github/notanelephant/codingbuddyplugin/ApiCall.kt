package com.github.notanelephant.codingbuddyplugin

import com.github.notanelephant.codingbuddyplugin.wrapper.GPT3Model
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest

class ApiCall {
    companion object{
        suspend fun getApiResponse(openAI: OpenAIClient, prompt: String, code: String = ""): String {
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
}