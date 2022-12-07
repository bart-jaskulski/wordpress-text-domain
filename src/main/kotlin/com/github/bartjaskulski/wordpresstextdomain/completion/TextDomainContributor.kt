package com.github.bartjaskulski.wordpresstextdomain.completion

import com.github.bartjaskulski.wordpresstextdomain.util.MethodMatcher
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class TextDomainContributor : CompletionContributor() {
    private val stringPattern: ElementPattern<PsiElement> =
        PlatformPatterns.or(
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL),
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE)
        )

    init {
        extend(CompletionType.BASIC, stringPattern, DefaultProvider)
    }

    object DefaultProvider : CompletionProvider<CompletionParameters>() {

        /** Map of <wantIndex, signatures> **/
        private val signaturesMap = mapOf(
            1 to listOf(
                MethodMatcher.CallSignature("__"),
                MethodMatcher.CallSignature("_e"),
                MethodMatcher.CallSignature("esc_html__"),
                MethodMatcher.CallSignature("esc_html_e"),
                MethodMatcher.CallSignature("esc_attr__"),
                MethodMatcher.CallSignature("esc_attr_e"),
            ),
            2 to listOf(
                MethodMatcher.CallSignature("_n_noop"),
                MethodMatcher.CallSignature("_ex"),
                MethodMatcher.CallSignature("_x"),
                MethodMatcher.CallSignature("esc_html_x"),
                MethodMatcher.CallSignature("esc_attr_x"),
            ),
            3 to listOf(
                MethodMatcher.CallSignature("_nx_noop"),
                MethodMatcher.CallSignature("_n"),
            ),
            4 to listOf(
                MethodMatcher.CallSignature("_nx"),
            )
        )

        private val textDomainPattern = Regex("Text Domain:\\s+([\\w-]+)")

        override fun addCompletions(
            parameters: CompletionParameters,
            context: ProcessingContext,
            result: CompletionResultSet
        ) {
            val psiElement = parameters.originalPosition
            if (psiElement === null) return
            if (!isStringBasedParameter(psiElement)) return

            var matchParameters: MethodMatcher.MatchParameter? = null
            for (signatureSet in signaturesMap) {
                val wantIndex = signatureSet.key
                matchParameters = MethodMatcher.StringMethodParameterMatcher(psiElement, wantIndex)
                    .withSignature(signatureSet.value)
                    .match()

                if (matchParameters !== null) break
            }

            if (matchParameters == null) return

            val module = ModuleUtil.findModuleForPsiElement(psiElement)
            if (module === null) return

            val possibleFiles = mutableListOf<VirtualFile>()
            for (root in module.rootManager.contentRoots) {
                val matchingFiles = root.children.filter {
                    it.name == "${root.name}.php" || it.name == "style.css" || it.extension == "php"
                }
                possibleFiles.addAll(matchingFiles)
            }

            if (possibleFiles.isEmpty()) return
            val suggestions = mutableListOf<LookupElement>()
            for (file in possibleFiles) {
                // Read only 8Kb as WordPress does.
                val inputStream = file.inputStream.use { it.readNBytes(8000).inputStream() }
                val string = inputStream.bufferedReader().use { it.readText() }
                val found = textDomainPattern.find(string)

                if (found != null) {
                    val completionValue = found.groups[1]?.value
                    if (completionValue != null) {
                        suggestions.add(LookupElementBuilder.create(completionValue))
                    }
                }
            }
            result.addAllElements(suggestions)
        }

        private fun isStringBasedParameter(psiElement: PsiElement?) =
            psiElement !== null && psiElement.context is StringLiteralExpression
    }


}