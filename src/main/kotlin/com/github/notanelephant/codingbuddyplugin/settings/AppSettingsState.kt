package com.github.notanelephant.codingbuddyplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "com.github.notanelephant.codingbuddyplugin.settings.AppSettingsState",
    storages = [Storage("CodingBuddyPluginSettings.xml")]
)
internal class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    private val defaultTodoKeyword = "TODO"
    var todoKeyword: String = defaultTodoKeyword
        get() = field.trim().ifBlank { defaultTodoKeyword }

    var unitTestPreferredFramework = ""
    var apiKey = ""
    
    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}
