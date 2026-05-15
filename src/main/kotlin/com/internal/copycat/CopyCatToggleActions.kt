package com.internal.copycat

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class ToggleIncludeFilePathAction : ToggleAction("Include file path") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.includeFilePath
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.includeFilePath = state }
}

class ToggleNoCommentsAction : ToggleAction("Do not copy comments") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.doNotCopyComments
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.doNotCopyComments = state }
}

class ToggleNoPackageImportsAction : ToggleAction("Do not copy package name and imports") {
    override fun isSelected(e: AnActionEvent) = CopyCatSettings.doNotCopyPackageAndImports
    override fun setSelected(e: AnActionEvent, state: Boolean) { CopyCatSettings.doNotCopyPackageAndImports = state }
}