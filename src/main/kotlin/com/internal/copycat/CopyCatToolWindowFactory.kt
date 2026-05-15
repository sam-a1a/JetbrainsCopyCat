package com.internal.copycat

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class CopyCatToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val tabs = com.intellij.ui.components.JBTabbedPane()
        tabs.addTab("Options", CopyCatSettingsPanel())
        tabs.addTab("Lines of Code", CopyCatLocPanel(project))

        val content = ContentFactory.getInstance().createContent(tabs, "", false)
        toolWindow.contentManager.addContent(content)
    }
}