package pl.airq.ga.radon.adapters.evolution

import pl.airq.ga.radon.domain.model.TrainingData

internal interface ProblemEvaluation<G> {
    fun evaluate(genotype: G, trainingData: TrainingData): Double
}
