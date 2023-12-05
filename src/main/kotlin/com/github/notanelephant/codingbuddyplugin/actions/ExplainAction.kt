package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.SupportedFiles
import com.github.notanelephant.codingbuddyplugin.toolWindow.MyToolWindowFactory.MyToolWindow.Companion.setTextAreaTextWithApiCall
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys


class ExplainAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        //get event.project as currentproject, and if it is null, show an error popup and return
        val currentProject = event.project ?: run {
            ErrorDialog.show(null, "No project found")
            return
        }
        
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        //if the editor has a selection, run the setTextAreaText function with the selected text
        if(editor?.selectionModel?.selectedText != null) {
            editor.selectionModel.selectedText?.let { selectedText ->
                setTextAreaTextWithApiCall(currentProject, "Explain the given code", selectedText)
            }
        }
        //else if: run the setTextAreaText function with the virtual file text
        else if(event.getData(CommonDataKeys.VIRTUAL_FILE)?.extension?.lowercase() in SupportedFiles.extensions){
            event.getData(CommonDataKeys.VIRTUAL_FILE)?.let {virtualFile ->
                setTextAreaTextWithApiCall(currentProject, "Explain the given code", virtualFile.inputStream.bufferedReader().use { it.readText() })
            }
        }
        
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        // Check if there is an editor and there's a selection in it
        val isTextSelected = editor?.selectionModel?.hasSelection() == true
        
        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected || virtualFile.let {it?.extension?.lowercase() in SupportedFiles.extensions }
    }
}