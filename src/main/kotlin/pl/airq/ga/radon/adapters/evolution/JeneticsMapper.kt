package pl.airq.ga.radon.adapters.evolution

import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.Phenotype
import pl.airq.ga.radon.domain.model.Timestamp
import pl.airq.ga.radon.domain.model.TrainingData
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeMapper
import java.time.Instant
import java.util.stream.Collectors

internal class JeneticsMapper : AirqPhenotypeMapper<Phenotype<DoubleGene, Double>> {
    override fun from(phenotype: Phenotype<DoubleGene, Double>, trainingData: TrainingData): AirqPhenotype {
        val values = phenotype.genotype()
            .chromosome()
            .stream()
            .map { obj: DoubleGene -> obj.floatValue() }
            .collect(Collectors.toList())
        return AirqPhenotype(
            Timestamp.from(Instant.now()),
            trainingData.sensorId,
            trainingData.fields,
            values,
            trainingData.predictionConfig,
            phenotype.fitness()
        )
    }

    override fun to(airqPhenotype: AirqPhenotype, minValue: Int, maxValue: Int): Phenotype<DoubleGene, Double> {
        val genes: List<DoubleGene> = airqPhenotype.values.stream()
            .map { DoubleGene.of(it.toDouble(), minValue.toDouble(), maxValue.toDouble()) }
            .collect(Collectors.toUnmodifiableList())
        return Phenotype.of(Genotype.of(DoubleChromosome.of(genes)), 1)
    }
}
