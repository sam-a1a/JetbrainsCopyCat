package com.internal.copycat

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyCatAction : AnAction() {

    private val EXCLUDED_EXTENSIONS = setOf(
        "json", "xml", "lock", "yml", "yaml", "md", "txt", "gradle",
        "properties", "toml", "iml", "png", "jpg", "jpeg", "gif",
        "svg", "ico", "pdf", "zip", "jar", "class"
    )

    override fun actionPerformed(e: AnActionEvent) {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return
        val project = e.project ?: return
        val selectedText = if (CopyCatSettings.copyOnlySelectedLines)
            e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText
        else null

        val content = StringBuilder()
        var totalLines = 0
        var totalChars = 0
        var totalFiles = 0

        for (file in files) {
            collectContent(file, project, selectedText, content) { lines, chars, count ->
                totalLines += lines
                totalChars += chars
                totalFiles += count
            }
        }

        Toolkit.getDefaultToolkit().systemClipboard
            .setContents(StringSelection(content.toString()), null)

        if (CopyCatSettings.showLineCount) {
            WindowManager.getInstance().getStatusBar(project)?.info =
                "CopyCat: copied $totalLines lines, $totalChars chars across $totalFiles file(s)"
        }
    }

    private fun collectContent(
        file: VirtualFile,
        project: Project,
        selectedText: String?,
        content: StringBuilder,
        stats: (Int, Int, Int) -> Unit
    ) {
        if (file.isDirectory) {
            for (child in file.children) {
                if (CopyCatSettings.excludeHiddenFiles && child.name.startsWith(".")) continue
                collectContent(child, project, selectedText, content, stats)
            }
            return
        }

        if (CopyCatSettings.excludeHiddenFiles && file.name.startsWith(".")) return
        if (CopyCatSettings.excludeFileTypes && EXCLUDED_EXTENSIONS.contains(file.extension?.lowercase())) return

        var text = selectedText ?: getFormattedContent(file, project)

        if (CopyCatSettings.excludeLargeFiles && text.lines().size > 500) return

        when {
            CopyCatSettings.copyAsPlainText -> {
                text = removeComments(text)
                text = removePackageAndImports(text)
                text = removeBlankLines(text)
            }
            else -> {
                if (CopyCatSettings.doNotCopyComments) text = removeComments(text)
                if (CopyCatSettings.doNotCopyPackageAndImports) text = removePackageAndImports(text)
            }
        }

        val header = buildString {
            if (CopyCatSettings.includeFilePath) appendLine("// ${file.path}")
            else if (CopyCatSettings.includeFileName) appendLine("// ${file.name}")
        }

        val block = if (CopyCatSettings.copyAsMarkdown) {
            "$header```${getLanguage(file)}\n$text\n```\n\n"
        } else {
            "$header$text\n"
        }

        content.append(block)
        stats(text.lines().size, text.length, 1)
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

    private fun removeComments(text: String): String {
        var r = text
        r = r.replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
        r = r.replace(Regex("//[^\n]*"), "")
        r = r.replace(Regex("(?m)^\\s*#[^\n]*"), "")
        r = r.replace(Regex("<!--[\\s\\S]*?-->"), "")
        r = r.replace(Regex("\n{3,}"), "\n\n")
        return r
    }

    private fun removePackageAndImports(text: String): String {
        return text.lines()
            .filter { !it.trim().startsWith("package ") && !it.trim().startsWith("import ") }
            .joinToString("\n")
            .replace(Regex("\n{3,}"), "\n\n")
            .trimStart()
    }

    private fun removeBlankLines(text: String): String {
        return text.lines().filter { it.isNotBlank() }.joinToString("\n")
    }

    private fun getLanguage(file: VirtualFile) = when (file.extension?.lowercase()) {
        "kt", "kts" -> "kotlin"
        "java"      -> "java"
        "py"        -> "python"
        "js", "mjs" -> "javascript"
        "ts"        -> "typescript"
        "html"      -> "html"
        "css"       -> "css"
        "xml"       -> "xml"
        "json"      -> "json"
        "sh"        -> "bash"
        "sql"       -> "sql"
        "rb"        -> "ruby"
        "go"        -> "go"
        "rs"        -> "rust"
        "cpp", "cc" -> "cpp"
        "c"         -> "c"
        "cs"        -> "csharp"
        "php"       -> "php"
        "swift"     -> "swift"
        "yaml","yml"-> "yaml"
        else        -> ""
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = true
    }
}