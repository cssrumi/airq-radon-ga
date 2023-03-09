package pl.airq.ga.radon.adapters.prediction

import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.port.UniqueQueue
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PredictionMeasurementListener(
    private val predictionQueue: UniqueQueue<SensorId>
) : MeasurementListener {

    override fun listen(measurement: Measurement) {
        predictionQueue.put(measurement.sensorId)
    }

    override fun order() = 0
}
