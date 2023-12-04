package com.github.notanelephant.codingbuddyplugin.toolWindow

import com.github.notanelephant.codingbuddyplugin.ApiCall
import com.github.notanelephant.codingbuddyplugin.MyBundle
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
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val textArea: JTextArea = JTextArea(MyBundle.message("placeholder" )).apply {
            lineWrap = true
            wrapStyleWord = true
        }

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS) // Use BoxLayout with Y_AXIS alignment
            alignmentX = Component.LEFT_ALIGNMENT // Set horizontal alignment to LEFT

            add(JBScrollPane(textArea)) // Wrap the JTextArea in a JBScrollPane for scrolling
            add(Box.createVerticalGlue()) // Add glue to stretch vertically
            
        }
        
        companion object{
            @OptIn(DelicateCoroutinesApi::class)
            fun setTextAreaText(currentProject : Project, prompt: String, selectedText: String) {
                val toolWindowId = "Coding Buddy"
                
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
                                val explanation = ApiCall.getApiResponse(prompt, selectedText)
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
