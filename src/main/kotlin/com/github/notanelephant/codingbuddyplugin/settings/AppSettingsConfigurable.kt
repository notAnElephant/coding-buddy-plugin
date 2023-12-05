package com.github.notanelephant.codingbuddyplugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {

    private var mySettingsComponent: AppSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "SDK: Application Settings Example"
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        var modified = mySettingsComponent?.todoKeyword != settings.todoKeyword
        modified = modified or (mySettingsComponent?.alwaysSendWholeFile != settings.alwaysSendWholeFile)
        modified = modified or (mySettingsComponent?.unitTestPreferredFramework != settings.unitTestPreferredFramework)
        return modified
    }

    override fun apply() {
        val settings = AppSettingsState.instance
        settings.todoKeyword = mySettingsComponent?.todoKeyword ?: ""
        settings.unitTestPreferredFramework = mySettingsComponent?.unitTestPreferredFramework ?: ""
        settings.alwaysSendWholeFile = mySettingsComponent?.alwaysSendWholeFile ?: false
    }

    override fun reset() {
        val settings = AppSettingsState.instance
        mySettingsComponent?.todoKeyword = settings.todoKeyword
        mySettingsComponent?.unitTestPreferredFramework = settings.unitTestPreferredFramework
        mySettingsComponent?.alwaysSendWholeFile = settings.alwaysSendWholeFile
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
