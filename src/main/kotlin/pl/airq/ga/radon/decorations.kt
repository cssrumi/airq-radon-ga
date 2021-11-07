package pl.airq.ga.radon

import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

private val numberType = Number::class.createType()

fun KType.isNumber() = isSubtypeOf(numberType)
