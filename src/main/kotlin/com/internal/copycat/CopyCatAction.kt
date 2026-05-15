package com.internal.copycat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyCatAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val content = StringBuilder()

        for (file in files) {
            collectContent(file, content)
        }

        val selection = StringSelection(content.toString())
        Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
    }

    private fun collectContent(file: VirtualFile, content: StringBuilder) {
        if (file.isDirectory) {
            for (child in file.children) {
                collectContent(child, content)
            }
        } else {
            try {
                content.append(String(file.contentsToByteArray()))
            } catch (ignored: Exception) {
                // skip unreadable files
            }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = true
    }
}