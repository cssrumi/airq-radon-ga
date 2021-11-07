package pl.airq.ga.radon.adapters.measurement

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import pl.airq.ga.radon.domain.port.Store

internal class StoreMeasurementRepository(private val store: Store<SensorId, List<Measurement>>) :
    MeasurementRepository {

    override fun findAll(id: SensorId): List<Measurement>? = store.get(id)

    override fun save(measurement: Measurement): Measurement {
        val key = measurement.sensorId
        val measurements = store.get(key)?.toMutableList() ?: mutableListOf()
        measurements.add(measurement)
        store.put(key, measurements)
        return measurement
    }

    override fun findLatest(id: SensorId): Measurement? = store.get(id)?.last()

}
