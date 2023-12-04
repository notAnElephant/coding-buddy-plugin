package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall.getApiResponse
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
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

        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabled = isSourceCodeFileWithOneClass(virtualFile, event)
    }

    private fun isSourceCodeFileWithOneClass(virtualFile: VirtualFile?, event: AnActionEvent): Boolean {
        if (virtualFile == null || !virtualFile.isInLocalFileSystem) {
            return false
        }

        // Check if it's a source code file
        val supportedExtensions = setOf("java", "kt", "scala")
        val fileExtension = virtualFile.extension?.lowercase()
        if (fileExtension !in supportedExtensions) {
            return false
        }

        // Check if the file contains exactly one class
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
        psiFile?.let {
            val classes = it.children.filter { child -> child.node.elementType.toString() == "CLASS" }
            return classes.size == 1
        }

        return false
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}