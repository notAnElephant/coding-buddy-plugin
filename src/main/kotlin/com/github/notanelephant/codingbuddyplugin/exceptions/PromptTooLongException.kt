package com.github.notanelephant.codingbuddyplugin.exceptions

import java.security.InvalidParameterException

class PromptTooLongException(public val msg: String, public val maxCharLength: Int) : InvalidParameterException()