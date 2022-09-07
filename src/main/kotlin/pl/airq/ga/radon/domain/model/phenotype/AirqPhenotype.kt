package pl.airq.ga.radon.domain.model.phenotype

import io.quarkus.runtime.annotations.RegisterForReflection
import pl.airq.ga.radon.domain.model.PredictionConfig
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp

@RegisterForReflection
data class AirqPhenotype(
    val timestamp: Timestamp,
    val sensorId: SensorId,
    val fields: List<String>,
    val values: List<Float>,
    val prediction: PredictionConfig,
    val fitness: Double
) {
    fun fieldValueMap(): Map<String, Float> {
        val map: MutableMap<String, Float> = HashMap()
        for (i in fields.indices) {
            map[fields[i]] = values[i]
        }
        return map
    }
}
