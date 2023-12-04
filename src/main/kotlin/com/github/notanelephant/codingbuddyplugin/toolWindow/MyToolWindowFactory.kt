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
import java.awt.Component
import javax.swing.Box
import javax.swing.BoxLayout
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
        private val codeLabel: JTextArea = JTextArea(MyBundle.message("randomLabel", "?")).apply {
            lineWrap = true
            wrapStyleWord = true
            preferredSize = preferredSize.apply {
                width = 300
                height = 300 }
        }

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS) // Use BoxLayout with Y_AXIS alignment
            alignmentX = Component.LEFT_ALIGNMENT // Set horizontal alignment to LEFT

            add(JBScrollPane(codeLabel)) // Wrap the JTextArea in a JBScrollPane for scrolling
            add(Box.createVerticalGlue()) // Add glue to stretch vertically
            
        }
    }
}
