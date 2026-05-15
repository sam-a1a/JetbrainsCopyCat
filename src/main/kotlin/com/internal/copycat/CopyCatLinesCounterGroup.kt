package com.internal.copycat

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class CopyCatLinesCounterGroup : DefaultActionGroup("CopyCat Lines Counter", true) {

    private val fileIcon:   Icon? = tryIcon("/icons/file.svg")
    private val errorIcon:  Icon? = tryIcon("/icons/error.svg")
    private val folderIcon: Icon? = tryIcon("/icons/icon.svg")

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        e ?: return emptyArray()
        if (!CopyCatSettings.showLinesCounterInMenu) return emptyArray()
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) ?: return emptyArray()

        val actions = mutableListOf<AnAction>()
        for (file in files) {
            if (actions.isNotEmpty()) actions.add(Separator.getInstance())
            if (file.isDirectory) addFolderStats(file, actions)
            else addFileStats(file, actions)
        }
        return actions.toTypedArray()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = CopyCatSettings.showLinesCounterInMenu &&
                e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY) != null
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    private fun addFileStats(file: VirtualFile, list: MutableList<AnAction>) {
        try {
            val content    = String(file.contentsToByteArray())
            val total      = content.lines().size
            val noComments = stripComments(content).lines().count { it.isNotBlank() }
            list.add(label(file.name,                                                      fileIcon))
            list.add(label("Lines: $total total   |   $noComments without comments   (${total - noComments} in comments)", null))
            list.add(label("Size: ${formatSize(file.length)}",                             null))
        } catch (e: Exception) {
            list.add(label("Could not read ${file.name}", errorIcon))
        }
    }

    private fun addFolderStats(folder: VirtualFile, list: MutableList<AnAction>) {
        val files = mutableListOf<VirtualFile>()
        collectFiles(folder, files)

        var total = 0; var noComments = 0; var bytes = 0L
        for (f in files) {
            try {
                val c   = String(f.contentsToByteArray())
                total      += c.lines().size
                noComments += stripComments(c).lines().count { it.isNotBlank() }
                bytes      += f.length
            } catch (_: Exception) {}
        }

        list.add(label(folder.name,                                                               folderIcon))
        list.add(label("Lines: $total total   |   $noComments without comments   (${total - noComments} in comments)", null))
        list.add(label("Files: ${files.size}   |   Size: ${formatSize(bytes)}",                  null))
    }

    private fun collectFiles(dir: VirtualFile, result: MutableList<VirtualFile>) {
        for (child in dir.children) {
            if (child.isDirectory) collectFiles(child, result) else result.add(child)
        }
    }

    private fun label(text: String, icon: Icon?) = object : AnAction(text) {
        override fun actionPerformed(e: AnActionEvent) {}
        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = false
            icon?.let { e.presentation.icon = it }
        }
        override fun getActionUpdateThread() = ActionUpdateThread.BGT
    }

    private fun stripComments(text: String): String {
        var r = text
        r = r.replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
        r = r.replace(Regex("//[^\n]*"), "")
        r = r.replace(Regex("(?m)^\\s*#[^\n]*"), "")
        r = r.replace(Regex("<!--[\\s\\S]*?-->"), "")
        return r
    }

    private fun formatSize(bytes: Long) = when {
        bytes < 1024        -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else                -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }

    private fun tryIcon(path: String): Icon? = try {
        IconLoader.getIcon(path, this::class.java)
    } catch (e: Exception) { null }
}