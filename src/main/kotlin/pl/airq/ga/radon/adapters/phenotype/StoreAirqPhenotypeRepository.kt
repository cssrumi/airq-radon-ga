package pl.airq.ga.radon.adapters.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import pl.airq.ga.radon.domain.port.Store

class StoreAirqPhenotypeRepository(
    private val store: Store<SensorId, AirqPhenotype>
) : AirqPhenotypeRepository {

    override fun save(phenotype: AirqPhenotype): AirqPhenotype {
        return phenotype.also { store.put(phenotype.sensorId, phenotype) }
    }

    override fun findAll(sensorId: SensorId): Set<AirqPhenotype> = store.getAll(sensorId).toSet()

    override fun findLatest(sensorId: SensorId): AirqPhenotype? = findAll(sensorId)
        .maxByOrNull { it.timestamp }

    override fun findBest(sensorId: SensorId): AirqPhenotype? = findAll(sensorId)
        .minByOrNull { it.fitness }
}
