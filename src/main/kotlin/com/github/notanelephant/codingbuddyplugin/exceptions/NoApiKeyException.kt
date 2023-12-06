package com.github.notanelephant.codingbuddyplugin.exceptions

class NoApiKeyException(error: String, val actionToTake: String) : Exception(error)