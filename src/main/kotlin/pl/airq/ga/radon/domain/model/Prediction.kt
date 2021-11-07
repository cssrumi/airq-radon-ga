package pl.airq.ga.radon.domain.model

import io.quarkus.runtime.annotations.RegisterForReflection
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

@RegisterForReflection
data class Prediction @JsonCreator constructor(
    @param:JsonProperty("timestamp") val timestamp: Timestamp,
    @param:JsonProperty("value") val value: Double,
    @param:JsonProperty("config") val config: PredictionConfig,
    @param:JsonProperty("sensorId") val sensorId: SensorId
)
