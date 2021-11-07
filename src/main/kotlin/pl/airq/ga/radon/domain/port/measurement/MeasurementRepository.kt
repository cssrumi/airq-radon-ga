package pl.airq.ga.radon.domain.port.measurement

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Measurement

interface MeasurementRepository {

    fun findAll(id: SensorId): List<Measurement>?

    fun save(measurement: Measurement): Measurement

    fun findLatest(id: SensorId): Measurement?

}
