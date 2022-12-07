package com.github.bartjaskulski.wordpresstextdomain.util

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class PhpElementsUtil {

    fun isFunctionReferenceInstanceOf(
        functionReference: FunctionReference,
        signature: MethodMatcher.CallSignature
    ): Boolean {
        return signature.function == functionReference.name
    }

    fun getFunctionParameterReferenceBag(
        psiElement: PsiElement,
        wantIndex: Int
    ): FunctionReferenceBag? {
        val functionReference = PsiTreeUtil.getParentOfType(psiElement, FunctionReference::class.java)
        if (functionReference !is FunctionReference) return null

        val parameterList = functionReference.parameterList ?: return null
        val currentIndex = this.getCurrentParameterIndex(parameterList, psiElement) ?: return null;

        if (wantIndex >= 0 && currentIndex.index != wantIndex) {
            return null;
        }

        return FunctionReferenceBag(
            parameterList,
            functionReference,
            currentIndex
        );

    }

    fun getCurrentParameterIndex(
        parameters: ParameterList,
        parameter: PsiElement
    ): ParameterBag? {
        for ((index, value) in parameters.parameters.withIndex()) {
            if (value == parameter) {
                return ParameterBag(index, value)
            }

            if (value == parameter.context) {
                return ParameterBag(index, value)
            }
        }

        return null;
    }

    fun getFunctionParameter(parameter: PsiElement): String? {
        if (parameter !is StringLiteralExpression) {
            return null
        }

        val stringValue = parameter.getText();
        val value = stringValue.substring(
            parameter.valueRange.startOffset,
            parameter.valueRange.endOffset
        );
        return value
    }
}