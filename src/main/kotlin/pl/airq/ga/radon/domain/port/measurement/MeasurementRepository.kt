package pl.airq.ga.radon.domain.port.measurement

import pl.airq.ga.radon.domain.model.Limits
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.model.SensorId

interface MeasurementRepository {

    fun findAll(id: SensorId, limits: Limits = Limits.empty()): List<Measurement>
    fun save(measurement: Measurement): Measurement
    fun findLatest(id: SensorId): Measurement?

}
