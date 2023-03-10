package pl.airq.ga.radon.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.*
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import pl.airq.ga.radon.domain.port.prediction.PredictionRepository
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PredictionService(
    private val predictionRepository: PredictionRepository,
    private val measurementRepository: MeasurementRepository,
    private val phenotypeRepository: AirqPhenotypeRepository,
    private val predictionConfig: PredictionConfig,
    private val measurementValueExtractor: MeasurementValueExtractor
) {
    private val searchBestSince = Duration.ofDays(1)

    fun predict(sensorId: SensorId): Prediction? {
        val measurement = measurementRepository.findLatest(sensorId)
            ?: return null
        val phenotype = phenotypeRepository.findBest(sensorId, searchBestSince)
            ?: phenotypeRepository.findLatest(sensorId)
            ?: return null
        val predictionValue = createPrediction(phenotype, measurement)
        val prediction = Prediction(Timestamp.now(), predictionValue, predictionConfig, sensorId)
        predictionRepository.save(prediction)

        LOGGER.info("New prediction created for: {} with value: ", sensorId.value, predictionValue)
        return prediction
    }

    private fun createPrediction(airqPhenotype: AirqPhenotype, measurement: Measurement): Double {
        return airqPhenotype.fields
            .map { getValue(measurement, it) }
            .zip(airqPhenotype.values).sumOf { it.first * it.second }
    }

    private fun getValue(measurement: Measurement, field: String): Double {
        return measurementValueExtractor.extractValue(measurement, field).toDouble()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PredictionService::class.java)
    }
}
