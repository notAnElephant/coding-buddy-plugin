package com.github.notanelephant.codingbuddyplugin.wrapper.impl

import com.github.notanelephant.codingbuddyplugin.wrapper.Completions
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIHTTPClient
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionResponse

internal class CompletionsImpl(private val httpClient: OpenAIHTTPClient) : Completions {
    override suspend fun createCompletion(request: CreateCompletionRequest): CreateCompletionResponse {
        return httpClient.post(request, COMPLETIONS_ENDPOINT)
    }

    companion object {
        private const val COMPLETIONS_ENDPOINT = "v1/completions"
    }
}
