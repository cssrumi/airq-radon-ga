package pl.airq.ga.radon.adapters.evolution

import com.google.common.base.Preconditions
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.TrainingData
import kotlin.math.abs

internal class JeneticsProblemEvaluation : ProblemEvaluation<Genotype<DoubleGene>> {
    override fun evaluate(genotype: Genotype<DoubleGene>, trainingData: TrainingData): Double {
        Preconditions.checkArgument(
            genotype.geneCount() == trainingData.rowSize,
            "Row size ${trainingData.rowSize} and genotype gene count ${genotype.geneCount()} doesn't match"
        )
        val rowSize: Int = trainingData.rowSize
        return trainingData.stream()
            .mapToDouble { row ->
                var result = 0.0
                for (i in 0 until rowSize) {
                    LOGGER.debug("{}, {}, {}", genotype.chromosome(), genotype.chromosome().length(), row.values)
                    result += abs(row.values[i] * genotype.chromosome().get(i).doubleValue())
                }
                abs(result - row.expectedValue)
            }
            .sum().div(trainingData.size())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JeneticsProblemEvaluation::class.java)
    }
}
