package com.internal.copycat

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*

class CopyCatSettingsPanel : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.empty(16)
        background = bg()

        addTitle("Copy Content")
        addCheckbox("Include file path",                            { CopyCatSettings.includeFilePath },            { CopyCatSettings.includeFilePath = it })
        addCheckbox("Include file name header",                     { CopyCatSettings.includeFileName },            { CopyCatSettings.includeFileName = it })
        addCheckbox("Copy as Markdown",                             { CopyCatSettings.copyAsMarkdown },             { CopyCatSettings.copyAsMarkdown = it })
        addCheckbox("Copy only selected lines",                     { CopyCatSettings.copyOnlySelectedLines },      { CopyCatSettings.copyOnlySelectedLines = it })

        addSpacer()
        addTitle("Filters")
        addCheckbox("Do not copy comments",                         { CopyCatSettings.doNotCopyComments },          { CopyCatSettings.doNotCopyComments = it })
        addCheckbox("Do not copy package name and imports",         { CopyCatSettings.doNotCopyPackageAndImports }, { CopyCatSettings.doNotCopyPackageAndImports = it })
        addCheckbox("Copy as plain text (no comments, no blanks)",  { CopyCatSettings.copyAsPlainText },            { CopyCatSettings.copyAsPlainText = it })
        addCheckbox("Exclude non-code files (.json, .xml, .lock…)", { CopyCatSettings.excludeFileTypes },           { CopyCatSettings.excludeFileTypes = it })
        addCheckbox("Exclude hidden files and folders",             { CopyCatSettings.excludeHiddenFiles },         { CopyCatSettings.excludeHiddenFiles = it })
        addLargeFilesRow()

        addSpacer()
        addTitle("Display")
        addCheckbox("Show line and char count after copy",          { CopyCatSettings.showLineCount },              { CopyCatSettings.showLineCount = it })

        addSpacer()
        addTitle("Menu")
        addCheckbox("Show CopyCat Lines Counter in right-click menu", { CopyCatSettings.showLinesCounterInMenu },  { CopyCatSettings.showLinesCounterInMenu = it })

        addSpacer()
        addDivider()
        addSpacer()
        addGithubLink()

        add(Box.createVerticalGlue())
    }

    // Rows

    private fun addTitle(text: String) {
        val label = JLabel(text).apply {
            font = font.deriveFont(Font.BOLD, 11f)
            foreground = UIManager.getColor("Label.disabledForeground")
            alignmentX = LEFT_ALIGNMENT
            border = JBUI.Borders.emptyBottom(4)
        }
        add(label)
    }

    private fun addCheckbox(text: String, getter: () -> Boolean, setter: (Boolean) -> Unit) {
        val cb = JCheckBox(text, getter()).apply {
            background = bg()
            alignmentX = LEFT_ALIGNMENT
            addActionListener { setter(isSelected) }
        }
        add(cb)
        add(Box.createVerticalStrut(2))
    }

    private fun addLargeFilesRow() {
        val row = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = bg()
            alignmentX = LEFT_ALIGNMENT
            maximumSize = Dimension(Int.MAX_VALUE, 28)
        }
        val cb = JCheckBox(
            "Exclude files over ${CopyCatSettings.largeFileThreshold} lines",
            CopyCatSettings.excludeLargeFiles
        ).apply {
            background = bg()
            addActionListener { CopyCatSettings.excludeLargeFiles = isSelected }
        }
        val changeLabel = JLabel("Change").apply {
            foreground = JBColor(0x2D7DD2, 0x2D7DD2)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            border = JBUI.Borders.emptyLeft(8)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    val input = JOptionPane.showInputDialog(
                        this@CopyCatSettingsPanel,
                        "Enter max lines threshold:",
                        CopyCatSettings.largeFileThreshold.toString()
                    )
                    input?.toIntOrNull()?.takeIf { it > 0 }?.let {
                        CopyCatSettings.largeFileThreshold = it
                        cb.text = "Exclude files over $it lines"
                    }
                }
            })
        }
        row.add(cb)
        row.add(changeLabel)
        add(row)
        add(Box.createVerticalStrut(2))
    }

    private fun addDivider() {
        val sep = JSeparator().apply {
            maximumSize = Dimension(Int.MAX_VALUE, 1)
            alignmentX = LEFT_ALIGNMENT
        }
        add(sep)
    }

    private fun addGithubLink() {
        val row = JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)).apply {
            background = bg()
            alignmentX = LEFT_ALIGNMENT
            maximumSize = Dimension(Int.MAX_VALUE, 28)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }

        val text = JLabel("Contributions, feature requests, and bug reports are welcome on the GitHub repo").apply {
            foreground = JBColor(0x2D7DD2, 0x2D7DD2)
            font = font.deriveFont(11f)
        }

        val iconLabel = try {
            JLabel(com.intellij.openapi.util.IconLoader.getIcon("/icons/out.svg", this::class.java))
        } catch (_: Exception) {
            JLabel("↗")
        }

        row.add(text)
        row.add(iconLabel)

        val openGithub = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                Desktop.getDesktop().browse(URI("https://github.com/sam-a1a/JetbrainsCopyCat"))
            }
        }
        row.addMouseListener(openGithub)
        text.addMouseListener(openGithub)
        iconLabel.addMouseListener(openGithub)

        add(row)
    }

    private fun addSpacer() = add(Box.createVerticalStrut(10))
    private fun bg() = UIManager.getColor("Panel.background") ?: JBColor.WHITE
}