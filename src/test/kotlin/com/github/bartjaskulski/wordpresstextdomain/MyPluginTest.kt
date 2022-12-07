package com.github.bartjaskulski.wordpresstextdomain

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testCompletion() {
        assertTrue(true)
    }

    override fun getTestDataPath() = "src/test/testData"
}
