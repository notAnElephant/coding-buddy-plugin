package com.github.notanelephant.codingbuddyplugin.exceptions

class NoApiKeyException(private val error: String, public val actionToTake: String) : Exception(error)