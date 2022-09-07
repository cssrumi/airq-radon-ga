package pl.airq.ga.radon.domain.port.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import java.time.Duration

interface AirqPhenotypeRepository {

    fun save(phenotype: AirqPhenotype): AirqPhenotype
    fun findAll(sensorId: SensorId): Set<AirqPhenotype>
    fun findLatest(sensorId: SensorId): AirqPhenotype?
    fun findBest(sensorId: SensorId, since: Duration? = null): AirqPhenotype?

}
