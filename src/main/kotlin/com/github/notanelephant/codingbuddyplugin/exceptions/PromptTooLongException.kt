package com.github.notanelephant.codingbuddyplugin.exceptions

import java.security.InvalidParameterException

class PromptTooLongException(val msg: String, val maxCharLength: Int) : InvalidParameterException()