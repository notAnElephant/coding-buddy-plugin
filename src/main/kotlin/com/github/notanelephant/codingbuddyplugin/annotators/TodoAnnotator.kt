package com.github.notanelephant.codingbuddyplugin.annotators

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement


class TodoAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        holder.newAnnotation(HighlightSeverity.WARNING, "Invalid code") // or HighlightSeverity.ERROR
            //.withFix( MyFix(element))
            .create();
        if (element is PsiComment) {
            val text = element.text
            if (text.contains("//TODO")) {
                // Add your custom annotation at the end of the line
                val endOffset = element.textRange.endOffset
                val annotationText = " Implement this using Coding Buddy+"
                holder.newAnnotation(HighlightSeverity.INFORMATION, annotationText)
                    .range(TextRange(endOffset, endOffset + annotationText.length))
                    .afterEndOfLine()
                    .create()
            }
        }
    }
}