package com.github.notanelephant.codingbuddyplugin.wrapper

import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionResponse
import com.github.notanelephant.codingbuddyplugin.wrapper.exception.OpenAIClientException
import com.github.notanelephant.codingbuddyplugin.wrapper.exception.OpenAIServerException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json

internal class OpenAIHTTPClient(config: OpenAIClientConfig) {
    private val httpClient = constructHttpClient(config)

    suspend inline fun post(
        request: CreateCompletionRequest?,
        endpoint: String,
    ): CreateCompletionResponse {
        return this.request(typeInfo<CreateCompletionResponse>()) {
            it.post {
                url(path = endpoint)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    suspend inline fun get(endpoint: String): CreateCompletionResponse {
        return this.request(typeInfo<CreateCompletionResponse>()) {
            it.get {
                url(path = endpoint)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    suspend inline fun delete(endpoint: String): CreateCompletionResponse {
        return this.request(typeInfo<CreateCompletionResponse>()) {
            it.delete {
                url(path = endpoint)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    suspend inline fun post(
        form: List<PartData>,
        endpoint: String,
    ): CreateCompletionResponse {
        return this.request(typeInfo<CreateCompletionResponse>()) {
            it.submitFormWithBinaryData(formData = form, url = endpoint)
        }
    }

    private suspend fun <T : Any> request(
        info: TypeInfo,
        block: suspend (HttpClient) -> HttpResponse,
    ): T {
        return try {
            val response = block(httpClient)

            @Suppress("UNCHECKED_CAST")
            when {
                response.instanceOf(info.type) -> response as T
                response.status.isSuccess() -> response.body(info)
                else -> throw OpenAIServerException(response.status.value, response.bodyAsText())
            }
        } catch (ex: Exception) {
            throw OpenAIClientException(throwable = ex)
        }
    }

    private fun constructHttpClient(config: OpenAIClientConfig): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                register(ContentType.Application.Json, KotlinxSerializationConverter(jsonConfiguration))
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
                filter { request ->
                    request.url.host.contains("ktor.io")
                }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken = config.token, refreshToken = "")
                    }
                }
            }

            install(HttpTimeout) {
                config.timeout?.connect?.let { connect ->
                    connectTimeoutMillis = connect.inWholeMilliseconds
                }

                config.timeout?.request?.let { request ->
                    requestTimeoutMillis = request.inWholeMilliseconds
                }

                config.timeout?.socket?.let { socket ->
                    socketTimeoutMillis = socket.inWholeMilliseconds
                }
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = OPENAI_URL
                }
            }
        }
    }

    companion object {
        private const val OPENAI_URL: String = "api.openai.com"

        private val jsonConfiguration =
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
            }
    }
}
