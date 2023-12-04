package com.github.notanelephant.codingbuddyplugin.services

import com.github.notanelephant.codingbuddyplugin.MyBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()
    
    fun getCodeExplanation(code: String) : String {
        //TODO call the OpenAI API to get the explanation
        return "TODO"
    }
}
