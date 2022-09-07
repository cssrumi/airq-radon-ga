package pl.airq.ga.radon.adapters.prediction

import pl.airq.ga.radon.domain.PredictionService
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PredictionMeasurementListener(
    private val predictionService: PredictionService
) : MeasurementListener {

    override fun listen(measurement: Measurement) {
        predictionService.predict(measurement.sensorId)
    }

    override fun order() = 0
}
