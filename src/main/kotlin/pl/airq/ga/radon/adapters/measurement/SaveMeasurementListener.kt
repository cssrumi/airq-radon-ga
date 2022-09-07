package pl.airq.ga.radon.adapters.measurement

import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class SaveMeasurementListener(
    private val measurementRepository: MeasurementRepository
) : MeasurementListener {

    override fun listen(measurement: Measurement) = measurementRepository.save(measurement).let { }
    override fun order(): Int = Int.MIN_VALUE
}
