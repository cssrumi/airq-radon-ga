package pl.airq.ga.radon.adapters.phenotype

import pl.airq.ga.radon.adapters.store.InMemoryStore
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class PhenotypeConfiguration {

    @Produces
    @Singleton
    fun inMemoryAirqPhenotypeRepository(): AirqPhenotypeRepository = InMemoryAirqPhenotypeRepository(InMemoryStore())

}
