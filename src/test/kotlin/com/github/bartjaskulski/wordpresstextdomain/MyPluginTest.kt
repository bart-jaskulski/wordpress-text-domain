package com.github.bartjaskulski.wordpresstextdomain

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testCompletion() {
//        myFixture.configureByFiles("domain/domain.php", "domain/translatable.php");
//        myFixture.copyDirectoryToProject("domain", "domain")
//        myFixture.complete(CompletionType.BASIC);
        val lookupElementStrings = myFixture.lookupElementStrings;
        assertNotNull(lookupElementStrings);
        lookupElementStrings?.contains("text-domain")?.let { assertTrue(it) };
//        assertSameElements(lookupElementStrings, "text-domain", "language", "message", "tab", "website");
    }

    override fun getTestDataPath() = "src/test/testData"
}
