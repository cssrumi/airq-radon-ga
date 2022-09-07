package pl.airq.ga.radon.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import pl.airq.ga.radon.config.GaProperties
import pl.airq.ga.radon.domain.model.phenotype.RadonPhenotypeMap
import pl.airq.ga.radon.infrastructure.util.toStringByReflection
import java.time.temporal.ChronoUnit
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@RegisterForReflection
data class Prediction @JsonCreator constructor(
    @param:JsonProperty("timestamp") val timestamp: Timestamp,
    @param:JsonProperty("value") val value: Double,
    @param:JsonProperty("config") val config: PredictionConfig,
    @param:JsonProperty("sensorId") val sensorId: SensorId
)

@RegisterForReflection
class PredictionConfig @JsonCreator constructor(
    @param:JsonProperty("timeframe") val timeframe: Long,
    @param:JsonProperty("timeUnit") val timeUnit: ChronoUnit,
    @param:JsonProperty("field") val field: String
) {
    override fun toString() = toStringByReflection()
}

@Dependent
class PredictionProducer {

    @Singleton
    @Produces
    fun predictionConfig(properties: GaProperties): PredictionConfig {
        val settings = properties.prediction()
        return PredictionConfig(settings.timeFrame(), settings.timeUnit(), RadonPhenotypeMap.FIELD)
    }

}
