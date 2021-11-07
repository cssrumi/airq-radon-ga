package pl.airq.ga.radon.adapters.evolution

import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.Phenotype
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStart
import io.jenetics.util.Factory
import io.jenetics.util.ISeq
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.config.Properties
import pl.airq.ga.radon.domain.model.TrainingData
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.evolution.EvolutionService
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeMapper

internal class JeneticsEvolutionService(
    private val problemEvaluation: ProblemEvaluation<Genotype<DoubleGene>>,
    private val mapper: AirqPhenotypeMapper<Phenotype<DoubleGene, Double>>,
    properties: Properties
) : EvolutionService {
    private val min = properties.phenotype().genotype().gene().min()
    private val max = properties.phenotype().genotype().gene().max()
    private val maximalPhenotypeAge = properties.phenotype().maximalAge()
    private val generations = properties.evolution().generations()
    private val populationSize = properties.evolution().populationSize()

    override fun compute(trainingData: TrainingData, phenotypes: Set<AirqPhenotype>?): AirqPhenotype {
        LOGGER.info(
            "Computing phenotype for station: ${trainingData.sensorId}. TrainingData size: ${trainingData.size()}",
        )
        val genotypeFactory: Factory<Genotype<DoubleGene>> = generateFactory(trainingData.rowSize)
        val engine: Engine<DoubleGene, Double> = Engine
            .builder({ problemEvaluation.evaluate(it, trainingData) }, genotypeFactory)
            .populationSize(populationSize)
            .maximalPhenotypeAge(maximalPhenotypeAge.toLong())
            .minimizing()
            .build()
        val best: Phenotype<DoubleGene, Double> = engine.stream(initPopulation(phenotypes ?: setOf()))
            .limit(generations)
            .collect(EvolutionResult.toBestEvolutionResult())
            .bestPhenotype()
        val mappedBest: AirqPhenotype = mapper.from(best, trainingData)
        LOGGER.info("Best phenotype found: {}", mappedBest)
        return mappedBest
    }

    private fun generateFactory(size: Int): Factory<Genotype<DoubleGene>> {
        val genes: MutableList<DoubleGene> = ArrayList(size)
        for (i in 0 until size) {
            genes.add(DoubleGene.of(min.toDouble(), max.toDouble()))
        }
        return Genotype.of(DoubleChromosome.of(genes))
    }

    private fun initPopulation(phenotypes: Set<AirqPhenotype>): EvolutionStart<DoubleGene, Double> {
        if (phenotypes.isEmpty()) {
            LOGGER.info("Initial population is empty.")
            return EvolutionStart.empty()
        }
        LOGGER.info("Initial population created!")
        return EvolutionStart.of(
            phenotypes.stream()
                .limit(5)
                .map { phenotype -> mapper.to(phenotype, min, max) }
                .collect(ISeq.toISeq()), 1
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JeneticsEvolutionService::class.java)
    }

}
