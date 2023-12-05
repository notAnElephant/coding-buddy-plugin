package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.actions.UnitTestsAction.Companion.isSupportedCodeFile
import com.github.notanelephant.codingbuddyplugin.toolWindow.MyToolWindowFactory.MyToolWindow.Companion.setTextAreaText
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile


class ExplainAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        //get event.project as currentproject, and if it is null, show an error popup and return
        val currentProject = event.project ?: run {
            ErrorDialog.show(null, "No project found")
            return
        }

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        //if the editor has a selection, run the setTextAreaText function with the selected text
        if (editor?.selectionModel?.selectedText != null) {
            editor.selectionModel.selectedText?.let { selectedText ->
                setTextAreaText(currentProject, "Explain the given code", selectedText)
            }
        }
        //else if: run the setTextAreaText function with the virtual file text
        else if (isProjectViewFileSupported(event)) {
            event.getData(CommonDataKeys.VIRTUAL_FILE)?.let { virtualFile ->
                val fileText = virtualFile.inputStream.bufferedReader().use { it.readText() }
                setTextAreaText(currentProject, "Explain the given code", fileText)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        // Check if there is an editor and there's a selection in it
        val isTextSelected = editor?.selectionModel?.hasSelection() == true

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected || isProjectViewFileSupported(event)
    }

    private fun isProjectViewFileSupported(event: AnActionEvent): Boolean {
        return getVirtualFile(event)?.let { isSupportedCodeFile(it).second } == true
    }

    private fun getVirtualFile(event: AnActionEvent): VirtualFile? {
        return event.project?.let {
            ProjectView.getInstance(it).currentProjectViewPane.selectedUserObjects.firstOrNull() as? VirtualFile
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}