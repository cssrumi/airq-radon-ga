package pl.airq.ga.radon.domain

import pl.airq.ga.radon.domain.model.Measurement
import javax.inject.Singleton
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface MeasurementValueExtractor {

    fun extractNumber(measurement: Measurement, field: String): Number

}

@Singleton
private class ReflectionBasedMeasurementValueExtractor : MeasurementValueExtractor {
    private val getterCache = mutableMapOf<String, KProperty1.Getter<Measurement, Any?>>()

    override fun extractNumber(measurement: Measurement, field: String): Number {
        val getter = getterCache.computeIfAbsent(field) { findGetter(field) }
        return getter.invoke(measurement) as Number
    }

    private fun findGetter(field: String): KProperty1.Getter<Measurement, Any?> {
        val property = Measurement::class.memberProperties.find { it.name == field }!!
        return property.getter
    }
}
