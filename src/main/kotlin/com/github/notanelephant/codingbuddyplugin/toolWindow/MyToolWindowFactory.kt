package com.github.notanelephant.codingbuddyplugin.toolWindow

import com.github.notanelephant.codingbuddyplugin.ApiCall
import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.MyBundle
import com.github.notanelephant.codingbuddyplugin.exceptions.NoApiKeyException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JTextArea


class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow()
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow {
        private val textArea: JTextArea = JTextArea(MyBundle.message("placeholder" )).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
        }

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS) // Use BoxLayout with Y_AXIS alignment
            alignmentX = Component.LEFT_ALIGNMENT // Set horizontal alignment to LEFT

            add(JBScrollPane(textArea)) // Wrap the JTextArea in a JBScrollPane for scrolling
            add(Box.createVerticalGlue()) // Add glue to stretch vertically
            
        }
        
        companion object{
            @OptIn(DelicateCoroutinesApi::class)
            fun setTextAreaTextWithApiCall(currentProject: Project, prompt: String, selectedText: String) {
                val toolWindowId = "Coding Buddy"
                
                val toolWindow = ToolWindowManager.getInstance(currentProject).getToolWindow(toolWindowId)
                val content = toolWindow?.contentManager?.getContent(0)

                if (content != null && content.component is JBPanel<*>) {
                    val component = content.component as JBPanel<*>
                    component.components.forEach {
                        if (it is JBScrollPane) {
                            val textArea = it.viewport.view as JTextArea
                            val apiKey = try {
                                ApiCall.getApiKey()
                            } catch (e: NoApiKeyException) {
                                ErrorDialog.show(currentProject, "${e.message}, ${e.actionToTake}")
                                return
                            }
                            // perform the API call on a background thread
                            GlobalScope.launch(Dispatchers.IO) {
                                val explanation = ApiCall.getApiResponse(apiKey, prompt, selectedText)
                                ApplicationManager.getApplication().invokeLater {
                                    textArea.text = explanation
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
