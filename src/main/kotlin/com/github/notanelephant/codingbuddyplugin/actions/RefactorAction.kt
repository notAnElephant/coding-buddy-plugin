package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall.getApiResponse
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RefactorAction : AnAction() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        editor?.selectionModel?.selectedText?.let {
           val result = Messages.showOkCancelDialog(
                currentProject,
                "Are you sure you want to refactor the selected code?",
                "Refactor Action",
                "OK",
                "Cancel",
                Messages.getInformationIcon()
            )
            if (result == Messages.OK) {
               GlobalScope.launch(Dispatchers.IO) {
                   val refactoredCode = getApiResponse("Refactor the following code. " +
                           "If it is not refactorable because of it's length or any other reason," +
                           " just return \"cannot be refactored\". If it has basic syntax errors, e.g. " +
                           "a closing bracket is missing, DO NOT replace them, as it is probably " +
                           "a part of a working code", it)
                   
                   if(refactoredCode == "cannot be refactored"){
                       Messages.showInfoMessage("The code cannot be refactored. The selection is probably too short -" +
                               " try selecting more of it.", "Refactor Action")
                       return@launch
                   }
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
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        // Check if there is an editor and there's a selection in it
        val isTextSelected = editor?.selectionModel?.hasSelection() == true

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected
    }
}