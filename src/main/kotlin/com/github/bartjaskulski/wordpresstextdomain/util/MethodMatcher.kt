package com.github.bartjaskulski.wordpresstextdomain.util

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.FunctionReference

class MethodMatcher {

    class CallSignature(val function: String) {}

    class StringMethodParameterMatcher(psiElement: PsiElement, parameterIndex: Int) : AbstractMethodParameterMatcher(psiElement, parameterIndex) {

        override fun match(): MatchParameter? {
            val bag = PhpElementsUtil().getFunctionParameterReferenceBag(psiElement, parameterIndex)
            if (bag === null) return null

            val matchedSignature = isCallTo(bag.functionReference)
            if (matchedSignature === null) return null

            return MatchParameter(
                matchedSignature,
                bag.parameterBag,
                bag.parameterList.parameters,
                bag.functionReference
            )
        }

    }

    abstract class AbstractMethodParameterMatcher(val psiElement: PsiElement, val parameterIndex: Int): ParameterMatcher {

        private val signatures = mutableListOf<CallSignature>()

        fun withSignature(signature: CallSignature): AbstractMethodParameterMatcher {
            signatures.add(signature)
            return this
        }

        fun withSignature(signature: Collection<CallSignature>): AbstractMethodParameterMatcher {
            signatures.addAll(signature)
            return this
        }

        fun isCallTo(functionReference: FunctionReference): CallSignature? {
            for (signature in signatures) {
                if (PhpElementsUtil().isFunctionReferenceInstanceOf(functionReference, signature)) {
                    return signature
                }
            }

            return null
        }
    }

    interface ParameterMatcher {
        fun match(): MatchParameter?
    }

    class MatchParameter(
        signature: CallSignature,
        parameterBag: ParameterBag,
        parameters: Array<PsiElement>,
        functionReference: FunctionReference
    ) {

    }
}