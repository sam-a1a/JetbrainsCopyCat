package com.internal.copycat

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class CopyCatSettingsPanel : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = BorderFactory.createEmptyBorder(16, 16, 16, 16)
        background = UIManager.getColor("Panel.background")

        addTitle("Copy Content")
        addCheckbox("Include file path",                   { CopyCatSettings.includeFilePath },            { CopyCatSettings.includeFilePath = it })
        addCheckbox("Include file name header",            { CopyCatSettings.includeFileName },            { CopyCatSettings.includeFileName = it })
        addCheckbox("Copy as Markdown",                    { CopyCatSettings.copyAsMarkdown },             { CopyCatSettings.copyAsMarkdown = it })
        addCheckbox("Copy only selected lines",            { CopyCatSettings.copyOnlySelectedLines },      { CopyCatSettings.copyOnlySelectedLines = it })

        addSpacer()
        addTitle("Filters")
        addCheckbox("Do not copy comments",                { CopyCatSettings.doNotCopyComments },          { CopyCatSettings.doNotCopyComments = it })
        addCheckbox("Do not copy package name and imports",{ CopyCatSettings.doNotCopyPackageAndImports }, { CopyCatSettings.doNotCopyPackageAndImports = it })
        addCheckbox("Copy as plain text (no comments, no blank lines)", { CopyCatSettings.copyAsPlainText }, { CopyCatSettings.copyAsPlainText = it })
        addCheckbox("Exclude non-code files (.json, .xml, .lock...)",   { CopyCatSettings.excludeFileTypes }, { CopyCatSettings.excludeFileTypes = it })
        addCheckbox("Exclude hidden files and folders",    { CopyCatSettings.excludeHiddenFiles },         { CopyCatSettings.excludeHiddenFiles = it })
        addLargeFilesRow()

        addSpacer()
        addTitle("Display")
        addCheckbox("Show line and char count after copy", { CopyCatSettings.showLineCount },              { CopyCatSettings.showLineCount = it })

        add(Box.createVerticalGlue())
    }

    private fun addTitle(text: String) {
        val label = JLabel(text)
        label.font = label.font.deriveFont(Font.BOLD, 12f)
        label.foreground = UIManager.getColor("Label.disabledForeground")
        label.alignmentX = LEFT_ALIGNMENT
        label.border = BorderFactory.createEmptyBorder(0, 0, 4, 0)
        add(label)
    }

    private fun addCheckbox(text: String, getter: () -> Boolean, setter: (Boolean) -> Unit) {
        val cb = JCheckBox(text, getter())
        cb.background = UIManager.getColor("Panel.background")
        cb.alignmentX = LEFT_ALIGNMENT
        cb.addActionListener { setter(cb.isSelected) }
        add(cb)
        add(Box.createVerticalStrut(2))
    }

    private fun addLargeFilesRow() {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.background = UIManager.getColor("Panel.background")
        panel.alignmentX = LEFT_ALIGNMENT
        panel.maximumSize = Dimension(Int.MAX_VALUE, 28)

        val cb = JCheckBox(
            "Exclude files over ${CopyCatSettings.largeFileThreshold} lines",
            CopyCatSettings.excludeLargeFiles
        )
        cb.background = UIManager.getColor("Panel.background")
        cb.addActionListener { CopyCatSettings.excludeLargeFiles = cb.isSelected }

        val changeLabel = JLabel("Change")
        changeLabel.foreground = Color(0x2D7DD2)
        changeLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        changeLabel.border = BorderFactory.createEmptyBorder(0, 8, 0, 0)
        changeLabel.addMouseListener(object : MouseAdapter() {
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

        panel.add(cb)
        panel.add(changeLabel)
        add(panel)
        add(Box.createVerticalStrut(2))
    }

    private fun addSpacer() {
        add(Box.createVerticalStrut(12))
    }
}