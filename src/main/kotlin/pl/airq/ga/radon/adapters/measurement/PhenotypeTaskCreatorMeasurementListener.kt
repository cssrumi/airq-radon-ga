package pl.airq.ga.radon.adapters.measurement

import pl.airq.ga.radon.domain.model.GeneratePhenotypeTask
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.port.UniqueQueue
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener

internal class PhenotypeTaskCreatorMeasurementListener(
    private val taskQueue: UniqueQueue<GeneratePhenotypeTask>
) : MeasurementListener {

    override fun listen(measurement: Measurement) = taskQueue.put(GeneratePhenotypeTask(measurement.sensorId))
    override fun order(): Int = Int.MAX_VALUE
}
