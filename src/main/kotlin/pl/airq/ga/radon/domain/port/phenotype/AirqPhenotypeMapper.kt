package pl.airq.ga.radon.domain.port.phenotype

import pl.airq.ga.radon.domain.model.TrainingData
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype

interface AirqPhenotypeMapper<P> {
    fun from(phenotype: P, trainingData: TrainingData): AirqPhenotype
    fun to(airqPhenotype: AirqPhenotype, minValue: Int, maxValue: Int): P
}
