package com.github.notanelephant.codingbuddyplugin.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel

    // Added components for new settings
    private val myTodoKeywordText = JBTextField()
    private val myUnitTestFrameworkText = JBTextField()
    private val apiKey = JBTextField("Your OpenAI API key")

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Enter TODO keyword: "), myTodoKeywordText, 1, false)
            .addLabeledComponent(JBLabel("Preferred unit test framework: "), myUnitTestFrameworkText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    // Getters and setters for the new settings
    var todoKeyword: String?
        get() = myTodoKeywordText.text
        set(newKeyword) {
            myTodoKeywordText.text = newKeyword
        }

    var unitTestPreferredFramework: String?
        get() = myUnitTestFrameworkText.text
        set(newFramework) {
            myUnitTestFrameworkText.text = newFramework
        }
    var apiKeyText: String?
        get() = apiKey.text
        set(newApiKey) {
            apiKey.text = newApiKey
        }
    
}
