package com.github.notanelephant.codingbuddyplugin.wrapper

import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionResponse
import com.github.notanelephant.codingbuddyplugin.wrapper.impl.OpenAIClientImpl

interface OpenAIClient : Completions

interface Completions {
    /**
     * Creates a completion request to the GPT-3 /completions endpoint
     *
     * @param request The given completion request object
     * @return The generated completion response
     */
    suspend fun createCompletion(request: CreateCompletionRequest): CreateCompletionResponse
}

fun OpenAIClient(config: OpenAIClientConfig): OpenAIClient {
    return OpenAIClientImpl(config)
}
