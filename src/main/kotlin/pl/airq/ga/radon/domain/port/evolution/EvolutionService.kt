package pl.airq.ga.radon.domain.port.evolution

import pl.airq.ga.radon.domain.model.TrainingData
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype

interface EvolutionService {
    fun compute(trainingData: TrainingData, phenotypes: Set<AirqPhenotype>?): AirqPhenotype
}
