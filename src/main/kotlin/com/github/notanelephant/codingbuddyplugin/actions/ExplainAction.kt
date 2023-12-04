package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall.Companion.getApiResponse
import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.wrapper.HttpTimeout
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClientConfig
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.swing.JTextArea
import kotlin.time.Duration.Companion.seconds


class ExplainAction : AnAction() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(event: AnActionEvent) {
        //get event.project as currentproject, and if it is null, show an error popup and return
        val currentProject = event.project ?: run {
            ErrorDialog.show(null, "No project found")
            return
        }

        val toolWindowId = "Coding Buddy"
        
        val message: StringBuilder = StringBuilder(event.presentation.text + " Selected!")
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        editor?.selectionModel?.selectedText?.let {selectedText -> 
            val apiKey = requireNotNull(System.getenv("OPENAI_API_KEY")) {
                ErrorDialog.show(currentProject, "OpenAI API key is not present or incorrect")
                "ERROR: OPENAI_API_KEY env variable not set"
            }
            val config =
                OpenAIClientConfig(
                    apiKey,
                    HttpTimeout(request = 60.seconds),
                )
            val openAI = OpenAIClient(config)

            val toolWindow = ToolWindowManager.getInstance(currentProject).getToolWindow(toolWindowId)
            val content = toolWindow?.contentManager?.getContent(0)

            if (content != null && content.component is JBPanel<*>) {
                val component = content.component as JBPanel<*>
                component.components.forEach {
                    println(it.javaClass)
                    if (it is JBScrollPane) {
                        val textArea = it.viewport.view as JTextArea
                        
                        // perform the API call on a background thread
                        GlobalScope.launch(Dispatchers.IO) {
                            val explanation = getApiResponse(openAI, "Explain this code briefly", selectedText)
                            ApplicationManager.getApplication().invokeLater {
                                textArea.text = explanation
                            }
                        }
                    }
                }
            }

        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        // Check if there is an editor and there's a selection in it
        val isTextSelected = editor?.selectionModel?.hasSelection() == true

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}