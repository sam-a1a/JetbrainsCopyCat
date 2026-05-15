package com.internal.copycat

import com.intellij.ide.util.PropertiesComponent

object CopyCatSettings {
    private const val KEY_PATH     = "copycat.includeFilePath"
    private const val KEY_COMMENTS = "copycat.noComments"
    private const val KEY_IMPORTS  = "copycat.noPackageImports"

    var includeFilePath: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_PATH, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_PATH, v)

    var doNotCopyComments: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_COMMENTS, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_COMMENTS, v)

    var doNotCopyPackageAndImports: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_IMPORTS, false)
        set(v) = PropertiesComponent.getInstance().setValue(KEY_IMPORTS, v)
}