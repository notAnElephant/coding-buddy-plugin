package com.github.notanelephant.codingbuddyplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

class ErrorDialog private constructor(private val project: Project?, private val message: String) {

    fun show() {
        Messages.showErrorDialog(project!!, message, "Unexpected error")
    }
    companion object {
        fun show(project: Project?, message: String) {
            ErrorDialog(project, message).show()
        }
    }
}