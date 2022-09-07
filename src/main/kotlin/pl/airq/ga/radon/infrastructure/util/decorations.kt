package pl.airq.ga.radon.infrastructure.util

import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties

private val numberType = Number::class.createType()

fun KType.isNumber() = isSubtypeOf(numberType)

fun Any.toStringByReflection(exclude: List<String> = listOf(), mask: List<String> = listOf()): String {
    val propsString = this::class.memberProperties
        .filter { exclude.isEmpty() || !exclude.contains(it.name) }
        .joinToString(", ") {
            val value = if (!mask.isEmpty() && mask.contains(it.name)) "****" else it.getter.call(this).toString()
            "${it.name}=${value}"
        };

    return "${this::class.simpleName} [${propsString}]"
}
