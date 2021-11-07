package pl.airq.ga.radon.domain.model

data class Measurement(
    val sensorId: SensorId,
    val radon: Float,
    val pressure: Float,
    val timestamp: Timestamp
)
