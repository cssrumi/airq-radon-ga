package pl.airq.ga.radon.domain.port.measurement

import pl.airq.ga.radon.domain.model.Measurement

interface MeasurementListener {

    fun listen(measurement: Measurement)
    fun order(): Int

}
