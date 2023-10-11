package com.github.notanelephant.codingbuddyplugin.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages


class UnitTestsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project
        val message: StringBuilder = StringBuilder(event.presentation.text + " Selected!")
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        editor?.selectionModel?.selectedText?.let {
           val result = Messages.showOkCancelDialog(
                currentProject,
                message.toString(),
                "Refactor Action",
                "OK",
                "Cancel",
                Messages.getInformationIcon()
            )
            if (result == Messages.OK) {
                //TODO send data to server
            
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