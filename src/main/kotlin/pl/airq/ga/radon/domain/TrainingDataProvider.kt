package pl.airq.ga.radon.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.*
import pl.airq.ga.radon.domain.model.phenotype.RadonPhenotypeMap
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import pl.airq.ga.radon.infrastructure.metrics.LogMetrics
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TrainingDataProvider(
    private val measurementRepository: MeasurementRepository,
    private val predictionConfig: PredictionConfig,
    private val limits: Limits
) {

    @LogMetrics(named = "provideTrainingData")
    fun provide(sensorId: SensorId, withPredictionAfter: Duration): TrainingData {
        val phenotypeMap = RadonPhenotypeMap.create()
        val trainingData = TrainingData(sensorId, phenotypeMap.getFields(), predictionConfig)
        val measurements = measurementRepository.findAll(sensorId, limits)
        LOGGER.info("Measurements found: {} for sensor: ", measurements.size, sensorId.value)
        for (measurement in measurements) {
            val closest = findClosest(measurement, measurements, withPredictionAfter) ?: continue
            val valueToPredict = phenotypeMap.valueToPredict(closest) ?: continue
            val valueArray = phenotypeMap.map(measurement) ?: continue
            TrainingDataRow(measurement.timestamp, valueArray, valueToPredict).let(trainingData::addData)
        }
        return trainingData
    }

    private fun findClosest(measurement: Measurement, from: List<Measurement>, withPredictionAfter: Duration)
            : Measurement? {
        val delta = Duration.ofMinutes(5).toSeconds()
        val min = measurement.timestamp.toInstant().plusSeconds(withPredictionAfter.seconds - delta)
        val max = measurement.timestamp.toInstant().plusSeconds(withPredictionAfter.seconds + delta)
        return from
            .filter { it.timestamp.toInstant().isAfter(min) }
            .filter { it.timestamp.toInstant().isBefore(max) }
            .let { if (it.isEmpty()) return null else it[it.size / 2] } // TODO: replace else block
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TrainingDataProvider::class.java)
    }

}
