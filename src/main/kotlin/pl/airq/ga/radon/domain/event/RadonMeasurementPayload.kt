package pl.airq.ga.radon.domain.event

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp

@JsonIgnoreProperties(ignoreUnknown = true)
data class RadonMeasurementPayload(
    @JsonProperty("time")
    @JsonDeserialize(using = TimestampDeserializer::class)
    val timestamp: Timestamp,
    val radon: Float,
    val pressure: Float,
    @JsonProperty("sensor_id")
    @JsonDeserialize(using = SensorIdDeserializer::class)
    val sensorId: SensorId
) {
    fun toMeasurement() = Measurement(sensorId, radon, pressure, timestamp)
}
