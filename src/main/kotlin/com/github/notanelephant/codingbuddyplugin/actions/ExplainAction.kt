package com.github.notanelephant.codingbuddyplugin.actions

import com.github.notanelephant.codingbuddyplugin.wrapper.GPT3Model
import com.github.notanelephant.codingbuddyplugin.wrapper.HttpTimeout
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClient
import com.github.notanelephant.codingbuddyplugin.wrapper.OpenAIClientConfig
import com.github.notanelephant.codingbuddyplugin.wrapper.completions.CreateCompletionRequest
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds


class ExplainAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val currentProject = event.project
        val message: StringBuilder = StringBuilder(event.presentation.text + " Selected!")
        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        editor?.selectionModel?.selectedText?.let {
           val result = Messages.showOkCancelDialog(
                currentProject,
                message.toString(),
                "Refactor Action",
                "OK",
                "Cancel",
                Messages.getInformationIcon()
            )
            if (result == Messages.OK) {
                val apiKey = requireNotNull(System.getenv("OPENAI_API_KEY")) { "ERROR: OPENAI_API_KEY env variable not set" }
                val config =
                    OpenAIClientConfig(
                        apiKey,
                        HttpTimeout(request = 60.seconds),
                    )
                val openAI = OpenAIClient(config)

                //call in a coroutine
                runBlocking {
                        completionsApiExample(openAI, it)
                }
            }
        }
    }
    private suspend fun completionsApiExample(openAI: OpenAIClient, code: String) {
        val model = GPT3Model.DAVINCI.modelName
        val prompt = "Explain the given code part: $code"
        val createCompletionResponse =
            openAI.createCompletion(
                CreateCompletionRequest(
                    model = model,
                    prompt = prompt,
                    maxTokens = 20,
                    temperature = 0.7,
                ),
            )

        val output = createCompletionResponse.choices.joinToString("\n") { it.text }.trim()

        printOutput(
            "CreateCompletion (/completions)",
            model,
            prompt,
            "Creates a completion for the provided prompt",
            output,
        )
    }

    private fun printOutput(
        api: String,
        model: String,
        input: String,
        task: String,
        output: String,
    ) {
        println("Calling the $api API with the model $model...")
        println("Input: $input\nTask: $task\nOutput:\n$output".trimEnd())
        println("====================================================================================================\n")
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val editor = CommonDataKeys.EDITOR.getData(event.dataContext)

        // Check if there is an editor and there's a selection in it
        val isTextSelected = editor?.selectionModel?.hasSelection() == true

        // Enable or disable the action based on the selection
        event.presentation.isEnabled = isTextSelected
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}