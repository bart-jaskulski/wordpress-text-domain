package com.github.bartjaskulski.wordpresstextdomain.util

import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.ParameterList

class FunctionReferenceBag(
    val parameterList: ParameterList,
    val functionReference: FunctionReference,
    val parameterBag: ParameterBag
) {

}