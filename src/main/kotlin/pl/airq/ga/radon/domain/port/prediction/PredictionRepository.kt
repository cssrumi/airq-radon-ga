package pl.airq.ga.radon.domain.port.prediction

import pl.airq.ga.radon.domain.model.Prediction
import pl.airq.ga.radon.domain.model.SensorId

interface PredictionRepository {

    fun save(prediction: Prediction): Prediction
    fun findLatest(sensorId: SensorId): Prediction?

}
