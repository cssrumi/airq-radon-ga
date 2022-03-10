package pl.airq.ga.radon.adapters.measurement

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import pl.airq.ga.radon.domain.port.Store
import java.time.Duration
import java.time.temporal.ChronoUnit

internal class StoreMeasurementRepository(private val store: Store<SensorId, Measurement>) :
    MeasurementRepository {

    override fun findAll(id: SensorId): List<Measurement> = store.getAll(id)

    override fun save(measurement: Measurement): Measurement {
        store.put(measurement.sensorId, measurement)
        return measurement
    }

    override fun findLatest(id: SensorId): Measurement? = store.get(id)

}
