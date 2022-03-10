package pl.airq.ga.radon.domain

import pl.airq.ga.radon.domain.exception.PhenotypeProcessingException
import pl.airq.ga.radon.domain.model.*
import pl.airq.ga.radon.domain.model.phenotype.RadonPhenotypeMap
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class TrainingDataProvider(
    private val measurementRepository: MeasurementRepository
) {

    fun provide(sensorId: SensorId, withPredictionAfter: Duration): TrainingData {
        val phenotypeMap = RadonPhenotypeMap.create()
        val predictionConfig = PredictionConfig(
            withPredictionAfter.toHours(),
            ChronoUnit.HOURS,
            phenotypeMap.fieldToPredict()
        )
        val trainingData = TrainingData(sensorId, phenotypeMap.getFields(), predictionConfig)
        val measurements = measurementRepository.findAll(sensorId)
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
            .filter { ed -> ed.timestamp.toInstant().isAfter(min) }
            .filter { ed -> ed.timestamp.toInstant().isBefore(max) }
            .let { if (it.isEmpty()) return null else it[it.size / 2] }
    }
}
