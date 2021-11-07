package pl.airq.ga.radon.domain.model.phenotype

import pl.airq.ga.radon.domain.exception.PhenotypeMappingException
import pl.airq.ga.radon.isNumber
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

abstract class PhenotypeMap<T> protected constructor(
    protected val clazz: KClass<*>,
    private val _properties: List<KProperty1<T, Number?>>,
    private val propertyToPredict: KProperty1<T, Number?>
) {
    private val size: Int = _properties.size

    fun valueToPredict(obj: T): Float? = propertyToPredict.get(obj)?.toFloat()

    fun fieldToPredict(): String = propertyToPredict.name

    fun map(obj: T): FloatArray? {
        val mapped = FloatArray(size)
        for (i in 0 until size) {
            val value = _properties[i].get(obj)?.toFloat() ?: return null
            mapped[i] = value
        }
        return mapped
    }

    fun getFields(): List<String> = _properties.map { it.name }

    companion object Utils {
        fun <T : Any> createPropertyList(clazz: KClass<T>, properties: List<String>): List<KProperty1<T, Number>> {
            val findFunction = { propName: String ->
                clazz.memberProperties.filter { it.returnType.isNumber() }.first { it.name == propName }
            }
            val list = properties.map { findFunction(it) }
            if (list.size != properties.size) throw PhenotypeMappingException(
                "Unable to create property list"
            )
            return list as List<KProperty1<T, Number>>
        }
    }

}
