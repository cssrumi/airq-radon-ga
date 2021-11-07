package pl.airq.ga.radon.adapters.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import pl.airq.ga.radon.domain.port.Store

class InMemoryAirqPhenotypeRepository(
    private val store: Store<SensorId, Set<AirqPhenotype>>
) : AirqPhenotypeRepository {

    override fun save(phenotype: AirqPhenotype): AirqPhenotype {
        val value = store.get(phenotype.sensorId)?.toMutableSet() ?: mutableSetOf()
        value.add(phenotype)
        return phenotype.also { store.put(phenotype.sensorId, value) }
    }

    override fun findByStationId(sensorId: SensorId): Set<AirqPhenotype>? = store.get(sensorId)

    override fun findLatestByStationId(sensorId: SensorId): AirqPhenotype? = findByStationId(sensorId)
        ?.maxByOrNull { it.timestamp }

    override fun findBestByStationId(sensorId: SensorId): AirqPhenotype? = findByStationId(sensorId)
        ?.minByOrNull { it.fitness }
}
