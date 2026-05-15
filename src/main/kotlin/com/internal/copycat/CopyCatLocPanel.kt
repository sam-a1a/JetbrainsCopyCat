package com.internal.copycat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class CopyCatLocPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val statsPanel  = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS); background = bg() }
    private val foldersPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS); background = bg() }
    private val scanButton  = JButton("Scan Project")

    init {
        background = bg()

        val topBar = JPanel(FlowLayout(FlowLayout.LEFT)).apply { background = bg() }
        scanButton.addActionListener { runScan() }
        topBar.add(scanButton)

        val scroll = JScrollPane(foldersPanel).apply {
            border = null
            background = bg()
            viewport.background = bg()
        }

        val center = JPanel(BorderLayout()).apply {
            background = bg()
            border = EmptyBorder(8, 12, 8, 12)
            add(statsPanel, BorderLayout.NORTH)
            add(scroll, BorderLayout.CENTER)
        }

        add(topBar, BorderLayout.NORTH)
        add(center, BorderLayout.CENTER)
    }

    private fun runScan() {
        scanButton.isEnabled = false
        scanButton.text = "Scanning..."
        statsPanel.removeAll(); statsPanel.revalidate(); statsPanel.repaint()
        foldersPanel.removeAll(); foldersPanel.revalidate(); foldersPanel.repaint()

        ApplicationManager.getApplication().executeOnPooledThread {
            val root = project.guessProjectDir() ?: return@executeOnPooledThread

            val folderStatsList = mutableListOf<FolderStats>()
            val rootFileStats   = mutableListOf<FileStats>()

            for (child in root.children) {
                if (child.name.startsWith(".")) continue
                if (child.isDirectory) folderStatsList.add(scanFolder(child))
                else scanFile(child)?.let { rootFileStats.add(it) }
            }

            val allFiles     = folderStatsList.flatMap { it.allFiles } + rootFileStats
            val totalLines   = allFiles.sumOf { it.totalLines }
            val noComments   = allFiles.sumOf { it.linesWithoutComments }
            val totalBytes   = allFiles.sumOf { it.sizeBytes }
            val codeFiles    = allFiles.filter { it.linesWithoutComments > 0 }
            val codeOnlyLines = codeFiles.sumOf { it.linesWithoutComments }

            SwingUtilities.invokeLater {
                statsPanel.removeAll()
                statsPanel.add(statRow("Total lines of code",        "$totalLines lines",    formatSize(totalBytes), null))
                statsPanel.add(statRow("Without comments",           "$noComments lines",    formatSize(totalBytes), "- ${totalLines - noComments} lines"))
                statsPanel.add(statRow("Code-only files total",      "$codeOnlyLines lines", "${codeFiles.size} files", "- ${totalLines - codeOnlyLines} lines"))
                statsPanel.add(Box.createVerticalStrut(8))
                statsPanel.add(JSeparator().apply { maximumSize = Dimension(Int.MAX_VALUE, 1) })
                statsPanel.add(Box.createVerticalStrut(8))

                foldersPanel.removeAll()
                foldersPanel.border = EmptyBorder(0, 0, 12, 0)
                for (fs in folderStatsList.sortedByDescending { it.totalLines }) {
                    foldersPanel.add(FolderChip(fs))
                    foldersPanel.add(Box.createVerticalStrut(4))
                }

                scanButton.isEnabled = true
                scanButton.text = "Scan Project"
                statsPanel.revalidate(); statsPanel.repaint()
                foldersPanel.revalidate(); foldersPanel.repaint()
            }
        }
    }

    private fun statRow(label: String, value: String, sub: String, diff: String?): JPanel {
        val row = JPanel(FlowLayout(FlowLayout.LEFT, 6, 2)).apply {
            background = bg()
            maximumSize = Dimension(Int.MAX_VALUE, 26)
            alignmentX = LEFT_ALIGNMENT
        }
        row.add(JLabel("$label:").apply { font = font.deriveFont(Font.BOLD) })
        row.add(JLabel(value))
        row.add(JLabel("($sub)").apply { foreground = UIManager.getColor("Label.disabledForeground") })
        if (diff != null) row.add(JLabel(diff).apply { foreground = Color(0x4CAF50) })
        return row
    }

    private fun scanFolder(folder: VirtualFile): FolderStats {
        val files = mutableListOf<FileStats>()
        collectFiles(folder, files)
        return FolderStats(folder, files, files.sumOf { it.totalLines }, files.sumOf { it.linesWithoutComments }, files.sumOf { it.sizeBytes })
    }

    private fun collectFiles(dir: VirtualFile, result: MutableList<FileStats>) {
        for (child in dir.children) {
            if (child.isDirectory) collectFiles(child, result)
            else scanFile(child)?.let { result.add(it) }
        }
    }

    private fun scanFile(file: VirtualFile): FileStats? {
        return try {
            val content = String(file.contentsToByteArray())
            val totalLines = content.lines().size
            var stripped = content
            stripped = stripped.replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
            stripped = stripped.replace(Regex("//[^\n]*"), "")
            stripped = stripped.replace(Regex("(?m)^\\s*#[^\n]*"), "")
            stripped = stripped.replace(Regex("<!--[\\s\\S]*?-->"), "")
            val noCommentLines = stripped.lines().count { it.isNotBlank() }
            FileStats(file, totalLines, noCommentLines, file.length)
        } catch (e: Exception) { null }
    }
}

// Chip components
class FolderChip(private val stats: FolderStats) : JPanel(BorderLayout()) {
    private var expanded = false
    private val filesContainer = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        background = bg()
        border = EmptyBorder(6, 20, 4, 0)
        isVisible = false
    }
    private val upIcon   = tryIcon("/icons/up.svg")
    private val downIcon = tryIcon("/icons/down.svg")
    private val toggleBtn = JLabel(downIcon ?: JLabel("▶").let { return@let null }.also { } ).apply {
        if (downIcon == null) { (this as JLabel).text = "▶" }
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        border = EmptyBorder(0, 8, 0, 0)
    }

    init {
        background = bg()
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground") ?: Color.GRAY, 1, true),
            EmptyBorder(6, 10, 6, 10)
        )
        alignmentX = LEFT_ALIGNMENT
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)

        val header = JPanel(BorderLayout()).apply { background = bg() }
        val info   = JPanel(FlowLayout(FlowLayout.LEFT, 6, 0)).apply { background = bg() }

        info.add(JLabel(stats.folder.name).apply { font = font.deriveFont(Font.BOLD) })
        info.add(separator())
        info.add(JLabel("${stats.totalLines} lines"))
        info.add(JLabel("(${formatSize(stats.sizeBytes)})").apply { foreground = UIManager.getColor("Label.disabledForeground") })

        header.add(info, BorderLayout.CENTER)
        header.add(toggleBtn, BorderLayout.EAST)

        for (fs in stats.allFiles.sortedByDescending { it.totalLines }) {
            filesContainer.add(FileChip(fs))
            filesContainer.add(Box.createVerticalStrut(3))
        }

        val toggle = MouseAdapter@ object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) { toggle() }
        }
        header.addMouseListener(toggle)
        toggleBtn.addMouseListener(toggle)

        add(header, BorderLayout.NORTH)
        add(filesContainer, BorderLayout.CENTER)
    }

    private fun toggle() {
        expanded = !expanded
        filesContainer.isVisible = expanded
        if (upIcon != null && downIcon != null) {
            toggleBtn.icon = if (expanded) upIcon else downIcon
        } else {
            (toggleBtn as JLabel).text = if (expanded) "▼" else "▶"
        }
        revalidate(); repaint()
        parent?.revalidate(); parent?.repaint()
    }
}

class FileChip(private val stats: FileStats) : JPanel(FlowLayout(FlowLayout.LEFT, 6, 2)) {
    init {
        background = bg()
        alignmentX = LEFT_ALIGNMENT
        maximumSize = Dimension(Int.MAX_VALUE, 26)

        add(JLabel(stats.file.name).apply { font = font.deriveFont(Font.BOLD) })
        add(separator())
        add(JLabel("${stats.totalLines} lines"))
        add(JLabel("(${stats.linesWithoutComments} without comments)").apply {
            foreground = UIManager.getColor("Label.disabledForeground")
        })
        add(separator())
        add(JLabel(formatSize(stats.sizeBytes)).apply { foreground = Color(0x2D7DD2) })
    }
}

// Data classes

data class FileStats(val file: VirtualFile, val totalLines: Int, val linesWithoutComments: Int, val sizeBytes: Long)
data class FolderStats(val folder: VirtualFile, val allFiles: List<FileStats>, val totalLines: Int, val linesWithoutComments: Int, val sizeBytes: Long)

// ── Helpers

private fun bg() = UIManager.getColor("Panel.background") ?: Color.WHITE

private fun separator() = JSeparator(JSeparator.VERTICAL).apply { preferredSize = Dimension(1, 12) }

private fun formatSize(bytes: Long) = when {
    bytes < 1024            -> "$bytes B"
    bytes < 1024 * 1024     -> "${bytes / 1024} KB"
    else                    -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
}

private fun tryIcon(path: String): Icon? = try {
    IconLoader.getIcon(path, FileChip::class.java)
} catch (e: Exception) { null }