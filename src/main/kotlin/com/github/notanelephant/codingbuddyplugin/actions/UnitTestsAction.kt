package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall
import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.SupportedFiles
import com.github.notanelephant.codingbuddyplugin.exceptions.NoApiKeyException
import com.github.notanelephant.codingbuddyplugin.settings.AppSettingsState
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//import com.github.notanelephant.codingbuddyplugin.


class UnitTestsAction : AnAction() {
    private var classImplementation: String = ""
    private var className: String? = ""

    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project ?: run {
            ErrorDialog.show(null, "No project found")
            return
        }
        if (className?.isBlank() == true || classImplementation.isBlank()) {
            ErrorDialog.show(currentProject, "No non-empty class found in the file")
            return
        }

        val result = Messages.showOkCancelDialog(
            currentProject,
            "Are you sure you want to generate unit tests for the following class: $className?",
            "Unit Test Action",
            "OK",
            "Cancel",
            Messages.getInformationIcon()
        )
        if (result == Messages.OK) {
            GlobalScope.launch(Dispatchers.IO) {
                val apiKey = try {
                    ApiCall.getApiKey()
                } catch (e: NoApiKeyException) {
                    ErrorDialog.show(currentProject, "${e.message}, ${e.actionToTake}")
                    return@launch
                }
                val unitTest = ApiCall.getApiResponse(
                    apiKey,
                    "Write unit tests for the following code${
                        if (AppSettingsState.instance.unitTestPreferredFramework.isNotBlank()) {
                            " using ${AppSettingsState.instance.unitTestPreferredFramework}"
                        } else {
                            ""
                        }
                    }. The class name should be " +
                            "${className}UnitTests", classImplementation
                )

                // Get the source file's virtual file
                val sourceFile = getVirtualFile(event)
                if(sourceFile == null){
                    ErrorDialog.show(currentProject, "No source file found")
                    return@launch
                }

                // Check if the source file is not null
                sourceFile.let {
                    val testsFileName = "${className}UnitTests.${it.extension}"

                    // Check if the tests file already exists
                    val testsFile = sourceFile.parent.findChild(testsFileName)

                    if (testsFile != null) {
                        //TODO  replace or create new file? show dialog

                    } else {
                        // Create a new file with unit tests
                        //TODO not this way, create a new file with some api, not like this
                        createFileWithUnitTests(sourceFile.parent, testsFileName, unitTest)
                        Messages.showInfoMessage("Unit tests generated successfully", "Unit Test Action")
                    }
                }
            }
        }
    }

    private fun createFileWithUnitTests(parentDirectory: VirtualFile, fileName: String, unitTest: String) {

        // Create a new file with unit tests
        val testsFile = parentDirectory.createChildData(this, fileName)
        testsFile.setBinaryContent(unitTest.toByteArray())
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val virtualFile = getVirtualFile(event)
        
        event.presentation.isEnabled = isSourceCodeFileWithOneClass(virtualFile, event)
    }
    private fun getVirtualFile(event: AnActionEvent): VirtualFile? {
        return event.getData(CommonDataKeys.VIRTUAL_FILE) ?: event.project?.let {
            ProjectView.getInstance(it).currentProjectViewPane.selectedUserObjects.firstOrNull() as? VirtualFile
        }
    }

    private fun isSourceCodeFileWithOneClass(virtualFile: VirtualFile?, event: AnActionEvent): Boolean {
        if (virtualFile == null || !virtualFile.isInLocalFileSystem) {
            return false
        }
        if (!isSupportedCodeFile(virtualFile).second) return false

        // Check if the file contains exactly one class
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
        psiFile?.let {
            val classes = it.children.filter { child -> child.node.elementType.toString() == "CLASS" }
            val isExactlyOneClass = classes.size == 1
            if (isExactlyOneClass) {
                classImplementation = classes[0].text
                className = extractClassName(classImplementation)
            }
            return isExactlyOneClass
        }

        return false
    }

    companion object {
        fun isSupportedCodeFile(virtualFile: VirtualFile): Pair<String, Boolean> {
            // Check if it's a source code file of the supported type
            val fileExtension = virtualFile.extension?.lowercase()

            val isSupported = fileExtension in SupportedFiles.extensions

            return ".$fileExtension" to isSupported
        }

        fun extractClassName(classImplementation: String): String? {
            // Use a regular expression to extract the class name
            val regex = Regex("class\\s+([A-Za-z_][A-Za-z0-9_]*)")
            val matchResult = regex.find(classImplementation)
            return matchResult?.groupValues?.get(1)
        }
    }
}