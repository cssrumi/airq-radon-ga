package pl.airq.ga.radon.domain.port.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype

interface AirqPhenotypeRepository {
    fun save(phenotype: AirqPhenotype): AirqPhenotype
    fun findByStationId(sensorId: SensorId): Set<AirqPhenotype>?
    fun findLatestByStationId(sensorId: SensorId): AirqPhenotype?
    fun findBestByStationId(sensorId: SensorId): AirqPhenotype?
}
