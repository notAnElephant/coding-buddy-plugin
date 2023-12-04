package com.github.notanelephant.codingbuddyplugin.toolWindow

import com.github.notanelephant.codingbuddyplugin.MyBundle
import com.github.notanelephant.codingbuddyplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JTextArea


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()
        private val codeLabel: JTextArea = JTextArea(MyBundle.message("randomLabel", "?"))

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = FlowLayout(FlowLayout.LEFT) // Use FlowLayout with LEFT alignment for multiline

            add(JBScrollPane(codeLabel)) // Wrap the JTextArea in a JBScrollPane for scrolling
            add(JButton(MyBundle.message("shuffle")).apply {
                addActionListener {
                    codeLabel.text = MyBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }

}
