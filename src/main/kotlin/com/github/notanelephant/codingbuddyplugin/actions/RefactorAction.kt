package com.github.notanelephant.codingbuddyplugin.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable


//it extends the AnAction class

class RefactorAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        // Using the event, create and show a dialog
        // Using the event, create and show a dialog
        val currentProject = event.project
        val message: StringBuilder = StringBuilder(event.presentation.text + " Selected!")
        // If an element is selected in the editor, add info about it.
        // If an element is selected in the editor, add info about it.
        val selectedElement: Navigatable? = event.getData(CommonDataKeys.NAVIGATABLE)
        message.append("\nSelected Element: ").append(selectedElement)
        val title: String = event.presentation.description
        Messages.showMessageDialog(
            currentProject,
            message.toString(),
            title,
            Messages.getInformationIcon()
        )
    }

    /**
     * Updates the presentation of the action. Default implementation does nothing.
     * Override this method to provide the ability to dynamically change action's
     * state and(or) presentation depending on the context (For example
     * when your action state depends on the selection you can check for
     * selection and change the state accordingly).
     *
     *
     *
     *
     * This method can be called frequently, and on UI thread.
     * This means that this method is supposed to work really fast,
     * no real work should be done at this phase. For example, checking selection in a tree or a list,
     * is considered valid, but working with a file system or PSI (especially resolve) is not.
     * If you cannot determine the state of the action fast enough,
     * you should do it in the [.actionPerformed] method and notify
     * the user that action cannot be executed if it's the case.
     *
     *
     *
     *
     * If the action is added to a toolbar, its "update" can be called twice a second, but only if there was
     * any user activity or a focus transfer. If your action's availability is changed
     * in absence of any of these events, please call `ActivityTracker.getInstance().inc()` to notify
     * action subsystem to update all toolbar actions when your subsystem's determines that its actions' visibility might be affected.
     *
     * @see .getActionUpdateThread
     */
    override fun update(event: AnActionEvent) {
        super.update(event)

        val currentProject = event.project
        event.presentation.setEnabledAndVisible(currentProject != null)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}