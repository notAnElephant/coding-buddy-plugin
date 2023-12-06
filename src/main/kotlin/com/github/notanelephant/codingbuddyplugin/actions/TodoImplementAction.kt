package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall
import com.github.notanelephant.codingbuddyplugin.ApiCall.getApiResponse
import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.actions.UnitTestsAction.Companion.isSupportedCodeFile
import com.github.notanelephant.codingbuddyplugin.exceptions.NoApiKeyException
import com.github.notanelephant.codingbuddyplugin.settings.AppSettingsState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TodoImplementAction : AnAction() {
    private var language: String = ""
    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)
        
        if(currentProject == null || editor == null) {
            return
        }

        editor.selectionModel.selectedText?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val todoKeyword = AppSettingsState.instance.todoKeyword
                val apiKey = try {
                    ApiCall.getApiKey()
                } catch (e: NoApiKeyException) {
                    ErrorDialog.show(currentProject, "${e.message}, ${e.actionToTake}")
                    return@launch
                }
                val refactoredCode = getApiResponse(
                    apiKey,
                    "Replace the //$todoKeyword comments with their implementations. " +
                            "This is a part of a working $language code, so do not remove or add anything else. " +
                            "Do not write any code the //$todoKeyword" +
                            " comment doesn't ask you to. Assume every variable you see is valid and you can use them." +
                            " The code: ", it
                )
                // Replace the selected code in the editor
                ApplicationManager.getApplication().invokeLater {
                    WriteCommandAction.runWriteCommandAction(currentProject) {
                        editor.document.replaceString(
                            editor.selectionModel.selectionStart,
                            editor.selectionModel.selectionEnd,
                            refactoredCode
                        )
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

        // Check if the selected text contains "TODOAI"
        val containsTodoAi =
            editor?.selectionModel?.selectedText?.contains(AppSettingsState.instance.todoKeyword) ?: false

        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val isSupportedPair = isSupportedCodeFile(virtualFile)

        isSupportedPair.first.let {
            language = it
        }

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected && containsTodoAi && isSupportedPair.second
    }
}