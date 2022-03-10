package pl.airq.ga.radon.adapters.phenotype

import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.Store
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class PhenotypeConfiguration {

    @Produces
    @Singleton
    fun inMemoryAirqPhenotypeRepository(store: Store<SensorId, AirqPhenotype>): AirqPhenotypeRepository =
        StoreAirqPhenotypeRepository(store)

}
