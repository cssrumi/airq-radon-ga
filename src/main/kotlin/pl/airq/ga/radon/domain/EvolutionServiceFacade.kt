package pl.airq.ga.radon.domain

import org.slf4j.LoggerFactory
import pl.airq.ga.radon.config.Properties
import pl.airq.ga.radon.domain.exception.PhenotypeProcessingException
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.TrainingData
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import pl.airq.ga.radon.domain.port.evolution.EvolutionService
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EvolutionServiceFacade(
    private val evolutionService: EvolutionService,
    private val trainingDataService: TrainingDataProvider,
    private val airqPhenotypeRepository: AirqPhenotypeRepository,
    properties: Properties
) {
    private val timeFrame: Duration = createTimeFrame(properties.prediction())

    private fun createTimeFrame(props: Properties.Prediction) = Duration.of(props.timeFrame(), props.timeUnit())

    fun generateNewPhenotype(sensorId: SensorId): AirqPhenotype? {
        val trainingData: TrainingData = try {
            trainingDataService.provide(sensorId, timeFrame)
        } catch (e: PhenotypeProcessingException) {
            LOGGER.warn("Unable to create training data for: {}.", sensorId.value, e)
            return null
        }
        if (trainingData.size() == 0L) {
            LOGGER.warn("TrainingData is empty. Process stopped...")
            return null
        }

        LOGGER.info("{} created for Sensor: {}.", trainingData, sensorId.value)
        val basePhenotypes: Set<AirqPhenotype> = basePhenotypes(sensorId)
        val newPhenotype: AirqPhenotype = evolutionService.compute(trainingData, basePhenotypes)
        LOGGER.info("New phenotype computed with fitness: {}", newPhenotype.fitness)
        airqPhenotypeRepository.save(newPhenotype)
        LOGGER.info("New phenotype has been saved")
        return newPhenotype
    }

    private fun basePhenotypes(sensorId: SensorId): Set<AirqPhenotype> {
        val phenotypes: MutableSet<AirqPhenotype> = HashSet()
        airqPhenotypeRepository.findBestByStationId(sensorId)?.let(phenotypes::add)
        airqPhenotypeRepository.findLatestByStationId(sensorId)?.let(phenotypes::add)
        if (phenotypes.isNotEmpty()) {
            LOGGER.info("Base phenotypes: {}", phenotypes.size)
        } else {
            LOGGER.info("Base phenotypes not found.")
        }
        return phenotypes
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EvolutionServiceFacade::class.java)
    }

}
