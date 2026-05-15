package com.internal.copycat

import com.intellij.ide.util.PropertiesComponent

object CopyCatSettings {
    private const val KEY_PATH           = "copycat.includeFilePath"
    private const val KEY_COMMENTS       = "copycat.noComments"
    private const val KEY_IMPORTS        = "copycat.noPackageImports"
    private const val KEY_MARKDOWN       = "copycat.copyAsMarkdown"
    private const val KEY_FILENAME       = "copycat.includeFileName"
    private const val KEY_EXCLUDE_TYPES  = "copycat.excludeFileTypes"
    private const val KEY_PLAIN_TEXT     = "copycat.copyAsPlainText"
    private const val KEY_LINE_COUNT     = "copycat.showLineCount"
    private const val KEY_HIDDEN         = "copycat.excludeHidden"
    private const val KEY_SELECTED_LINES = "copycat.copySelectedLines"
    private const val KEY_LARGE_FILES    = "copycat.excludeLargeFiles"
    private const val KEY_THRESHOLD      = "copycat.largeFileThreshold"

    var includeFilePath: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_PATH, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_PATH, v)

    var doNotCopyComments: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_COMMENTS, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_COMMENTS, v)

    var doNotCopyPackageAndImports: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_IMPORTS, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_IMPORTS, v)

    var copyAsMarkdown: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_MARKDOWN, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_MARKDOWN, v)

    var includeFileName: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_FILENAME, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_FILENAME, v)

    var excludeFileTypes: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_EXCLUDE_TYPES, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_EXCLUDE_TYPES, v)

    var copyAsPlainText: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_PLAIN_TEXT, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_PLAIN_TEXT, v)

    var showLineCount: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_LINE_COUNT, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_LINE_COUNT, v)

    var excludeHiddenFiles: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_HIDDEN, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_HIDDEN, v)

    var copyOnlySelectedLines: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_SELECTED_LINES, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_SELECTED_LINES, v)

    var excludeLargeFiles: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_LARGE_FILES, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_LARGE_FILES, v)

    var largeFileThreshold: Int
        get() = PropertiesComponent.getInstance().getInt(KEY_THRESHOLD, 500)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_THRESHOLD, v, 500)
}