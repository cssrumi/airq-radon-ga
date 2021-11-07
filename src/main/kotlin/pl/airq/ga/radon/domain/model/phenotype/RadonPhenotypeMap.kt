package pl.airq.ga.radon.domain.model.phenotype

import pl.airq.ga.radon.domain.exception.PhenotypeProcessingException
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.isNumber
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class RadonPhenotypeMap private constructor(properties: List<KProperty1<Measurement, Number>>) :
    PhenotypeMap<Measurement>(Measurement::class, properties, predictionProperty()) {

    companion object Factory {
        private val DEFAULT_MEASUREMENT_FIELDS = listOf("radon", "pressure")
        private const val FIELD = "radon"

        fun create(): RadonPhenotypeMap = withFields(DEFAULT_MEASUREMENT_FIELDS)

        private fun withFields(fields: List<String>): RadonPhenotypeMap {
            return RadonPhenotypeMap(Utils.createPropertyList(Measurement::class, fields))
        }

        private fun predictionProperty(): KProperty1<Measurement, Number?> {
            val property = Measurement::class.memberProperties
                .filter { it.returnType.isNumber() }
                .firstOrNull { it.name == FIELD }
                ?: throw PhenotypeProcessingException("Unable to find prediction property")
            return property as KProperty1<Measurement, Number?>
        }
    }
}
