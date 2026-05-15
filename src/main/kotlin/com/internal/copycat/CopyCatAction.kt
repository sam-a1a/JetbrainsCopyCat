package com.internal.copycat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyCatAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val project = e.project ?: return
        val content = StringBuilder()

        for (file in files) {
            collectContent(file, project, content)
        }

        Toolkit.getDefaultToolkit().systemClipboard
            .setContents(StringSelection(content.toString()), null)
    }

    private fun collectContent(file: VirtualFile, project: Project, content: StringBuilder) {
        if (file.isDirectory) {
            for (child in file.children) {
                collectContent(child, project, content)
            }
        } else {
            content.append(getFormattedContent(file, project))
        }
    }

    private fun getFormattedContent(file: VirtualFile, project: Project): String {
        return try {
            val psiFile = PsiManager.getInstance(project).findFile(file)
                ?: return String(file.contentsToByteArray())

            var formatted = psiFile.text

            WriteCommandAction.runWriteCommandAction(project) {
                val copy = psiFile.copy() as PsiFile
                CodeStyleManager.getInstance(project).reformat(copy)
                formatted = copy.text
            }

            formatted
        } catch (e: Exception) {
            try { String(file.contentsToByteArray()) } catch (ex: Exception) { "" }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = true
    }
}