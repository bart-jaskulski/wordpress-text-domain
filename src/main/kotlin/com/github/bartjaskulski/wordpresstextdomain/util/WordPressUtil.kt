package com.github.bartjaskulski.wordpresstextdomain.util

import com.intellij.openapi.roots.ModuleRootManager

class WordPressUtil {
    enum class WordPressModule { PLUGIN, THEME }

    fun detectWordPressModule(rootManager: ModuleRootManager): WordPressModule {
        for (root in rootManager.contentRoots) {
            if (root.findChild("style.css") !== null) return WordPressModule.THEME
        }
        return WordPressModule.PLUGIN
    }
}