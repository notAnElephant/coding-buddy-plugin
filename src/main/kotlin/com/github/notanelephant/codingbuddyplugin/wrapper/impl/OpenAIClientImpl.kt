package com.github.notanelephant.codingbuddyplugin.wrapper.impl

import com.github.notanelephant.codingbuddyplugin.wrapper.*

internal class OpenAIClientImpl(
    private val config: OpenAIClientConfig,
    private val httpClient: OpenAIHTTPClient = OpenAIHTTPClient(config),
) : OpenAIClient,
    Completions by CompletionsImpl(httpClient)
