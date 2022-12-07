package com.github.bartjaskulski.wordpresstextdomain.util

import com.intellij.psi.PsiElement

class ParameterBag(
    val index: Int,
    val psiElement: PsiElement
) {

    val value = PhpElementsUtil().getFunctionParameter(psiElement)
}