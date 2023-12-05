package com.github.notanelephant.codingbuddyplugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class UnitTestService(project: Project) {


    fun getRandomNumber() = (1..100).random()
    fun createUnitTest(): String {
        return "This is a unit test"
    }
}
