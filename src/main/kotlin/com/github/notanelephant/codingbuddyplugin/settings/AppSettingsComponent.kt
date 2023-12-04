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
    private val myUserNameText = JBTextField()
    private val myIdeaUserStatus = JBCheckBox("Do you use IntelliJ IDEA? ")

    // Added components for new settings
    private val myTodoKeywordText = JBTextField()
    private val myUnitTestFrameworkText = JBTextField()
    private val myAlwaysSendWholeFileCheckBox = JBCheckBox("Always send the whole file?")

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Enter TODO keyword: "), myTodoKeywordText, 1, false)
            .addLabeledComponent(JBLabel("Preferred unit test framework: "), myUnitTestFrameworkText, 1, false)
            .addComponent(myAlwaysSendWholeFileCheckBox, 1)
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

    var alwaysSendWholeFile: Boolean
        get() = myAlwaysSendWholeFileCheckBox.isSelected
        set(newValue) {
            myAlwaysSendWholeFileCheckBox.isSelected = newValue
        }
}
