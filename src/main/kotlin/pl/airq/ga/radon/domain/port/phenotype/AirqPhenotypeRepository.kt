package pl.airq.ga.radon.domain.port.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype

interface AirqPhenotypeRepository {
    fun save(phenotype: AirqPhenotype): AirqPhenotype
    fun findAll(sensorId: SensorId): Set<AirqPhenotype>
    fun findLatest(sensorId: SensorId): AirqPhenotype?
    fun findBest(sensorId: SensorId): AirqPhenotype?
}
