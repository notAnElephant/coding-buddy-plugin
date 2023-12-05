package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.ApiCall
import com.github.notanelephant.codingbuddyplugin.ErrorDialog
import com.github.notanelephant.codingbuddyplugin.SupportedFiles
import com.github.notanelephant.codingbuddyplugin.exceptions.NoApiKeyException
import com.github.notanelephant.codingbuddyplugin.notifications.NotificationHelper
import com.github.notanelephant.codingbuddyplugin.settings.AppSettingsState
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


//import com.github.notanelephant.codingbuddyplugin.


class UnitTestsAction : AnAction() {
    private var classImplementation: String = ""
    private var className: String? = ""
    private var language: String = ""
    private var testFilePostFix = "UnitTests"

    private var virtualFile: VirtualFile? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project ?: run {
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
            ProgressManager.getInstance().runProcess({
                val apiKey = try {
                    ApiCall.getApiKey()
                } catch (e: NoApiKeyException) {
                    ErrorDialog.show(currentProject, "${e.message}, ${e.actionToTake}")
                    return@runProcess
                }
                val indicator = ProgressManager.getInstance().progressIndicator
                indicator.text = "Activity text"
                indicator.fraction = 0.5 // Activity percentage


                // perform the API call on a background thread
                CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                    var unitTest = ApiCall.getApiResponse(
                        apiKey,
                        "Write unit tests for the following code${
                            if (AppSettingsState.instance.unitTestPreferredFramework.isNotBlank()) {
                                " using ${AppSettingsState.instance.unitTestPreferredFramework}"
                            } else {
                                ""
                            }
                        }. The class name should be " +
                                "${className}${testFilePostFix}", classImplementation
                    )
                    if (virtualFile == null) {
                        ErrorDialog.show(currentProject, "No source file found")
                        return@launch
                    } else {
                        val parentDirectory = virtualFile?.parent
                        if (parentDirectory != null) {
                            WriteCommandAction.runWriteCommandAction(currentProject) {
                                var fileName = "$className" + "$testFilePostFix$language"
                                val existingFile = parentDirectory.findChild(fileName)

                                if (existingFile != null) {
                                    val overwrite = Messages.showYesNoDialog(
                                        currentProject,
                                        "File $fileName already exists. Do you want to overwrite it?",
                                        "File Exists",
                                        Messages.getQuestionIcon()
                                    ) == Messages.YES

                                    if (!overwrite) {
                                        val newFileName = Messages.showInputDialog(
                                            currentProject,
                                            "Enter a new file (and class) name:",
                                            "New File And Class Name",
                                            Messages.getQuestionIcon()
                                        )

                                        fileName = if (!newFileName.isNullOrBlank()) {
                                            //if the file name doesn't end with the language extension, add it
                                            if (!newFileName.endsWith(language)) {
                                                "$newFileName$language"
                                            } else {
                                                newFileName
                                            }
                                        } else {
                                            return@runWriteCommandAction
                                        }
                                        //replace class name in unittest with the new filename (without the extension)
                                        unitTest = unitTest.replace(className!!+testFilePostFix, newFileName.removeSuffix(language))
                                    }
                                }
                                try {
                                    parentDirectory.createChildData(this, fileName)
                                        .setBinaryContent(unitTest.toByteArray())
                                    NotificationHelper.showNotification(
                                        currentProject,
                                        "Unit tests for $className have been generated and saved to $fileName")
                                } catch (ex: Exception) {
                                    ErrorDialog.show(currentProject,
                                        ex.message ?: ("Unknown error while creating the file. " +
                                                "Please make sure the file you're trying to create isn't open.")
                                    )
                                    ex.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }, ProgressWindow(true, currentProject))
        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        virtualFile = getVirtualFile(event)

        event.presentation.isEnabled = isSourceCodeFileWithOneClass(virtualFile, event)
    }

    private fun getVirtualFile(event: AnActionEvent): VirtualFile? {
        return ReadAction.compute<VirtualFile?, Throwable> {
            event.getData(CommonDataKeys.VIRTUAL_FILE) ?: event.project?.let {
                ProjectView.getInstance(it).currentProjectViewPane.selectedUserObjects.firstOrNull() as? VirtualFile
            }
        }
    }


    private fun isSourceCodeFileWithOneClass(virtualFile: VirtualFile?, event: AnActionEvent): Boolean {
        if (virtualFile == null || !virtualFile.isInLocalFileSystem) {
            return false
        }
        val isSupportedPair = isSupportedCodeFile(virtualFile)
        if (!isSupportedPair.second) return false

        language = isSupportedPair.first

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