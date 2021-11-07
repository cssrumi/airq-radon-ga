package pl.airq.ga.radon.domain.port

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Prediction
import pl.airq.ga.radon.domain.model.PredictionConfig

interface PredictionQuery {
    fun findLatest(sensorId: SensorId?): Prediction?
    fun findAll(sensorId: SensorId?): Set<Prediction?>?
    fun findLatestWithPredictionConfig(sensorId: SensorId?, config: PredictionConfig?): Prediction?
}
