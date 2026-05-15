package com.internal.copycat

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class ToggleIncludeFilePathAction : ToggleAction("Include file path") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.includeFilePath
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.includeFilePath = state }
}

class ToggleIncludeFileNameAction : ToggleAction("Include file name header") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.includeFileName
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.includeFileName = state }
}

class ToggleNoCommentsAction : ToggleAction("Do not copy comments") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.doNotCopyComments
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.doNotCopyComments = state }
}

class ToggleNoPackageImportsAction : ToggleAction("Do not copy package name and imports") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.doNotCopyPackageAndImports
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.doNotCopyPackageAndImports = state }
}

class ToggleCopyAsMarkdownAction : ToggleAction("Copy as Markdown") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.copyAsMarkdown
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.copyAsMarkdown = state }
}

class ToggleCopyAsPlainTextAction : ToggleAction("Copy as plain text (no comments, no blank lines)") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.copyAsPlainText
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.copyAsPlainText = state }
}

class ToggleExcludeFileTypesAction : ToggleAction("Exclude non-code files (.json, .xml, .lock...)") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.excludeFileTypes
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.excludeFileTypes = state }
}

class ToggleShowLineCountAction : ToggleAction("Show line and char count after copy") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.showLineCount
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.showLineCount = state }
}

class ToggleExcludeHiddenAction : ToggleAction("Exclude hidden files and folders") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.excludeHiddenFiles
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.excludeHiddenFiles = state }
}

class ToggleCopySelectedLinesAction : ToggleAction("Copy only selected lines") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.copyOnlySelectedLines
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.copyOnlySelectedLines = state }
}

class ToggleExcludeLargeFilesAction : ToggleAction("Exclude files over 500 lines") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.excludeLargeFiles
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.excludeLargeFiles = state }
}