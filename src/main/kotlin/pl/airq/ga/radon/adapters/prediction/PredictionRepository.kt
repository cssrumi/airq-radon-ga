package pl.airq.ga.radon.adapters.prediction

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import io.quarkus.panache.common.Sort
import pl.airq.ga.radon.domain.model.Prediction
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp
import pl.airq.ga.radon.domain.port.prediction.PredictionRepository
import pl.airq.ga.radon.infrastructure.mongo.MongoPredictionConfig
import pl.airq.ga.radon.infrastructure.mongo.MongoTimeSeries
import pl.airq.ga.radon.infrastructure.util.toStringByReflection
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class MongoPredictionRepository(
    private val repository: PanacheMongoRepository<MongoPrediction>
) : PredictionRepository {

    override fun save(prediction: Prediction): Prediction {
        val mongoPrediction = MongoPrediction.from(prediction)
        repository.persist(mongoPrediction)
        return prediction
    }

    override fun findLatest(sensorId: SensorId): Prediction? = repository
        .find("sensorId", Sort.descending("timestamp"), sensorId.value)
        .firstResult()
        ?.toPrediction()

}

@MongoEntity(collection = "predictions")
@MongoTimeSeries(timeField = "timestamp", metaField = "sensorId")
class MongoPrediction {
    lateinit var timestamp: Timestamp
    var value: Double = 0.0
    lateinit var config: MongoPredictionConfig
    lateinit var sensorId: SensorId

    fun toPrediction() = Prediction(timestamp, value, config.toPredictionConfig(), sensorId)

    override fun toString() = toStringByReflection()

    companion object {
        fun from(prediction: Prediction): MongoPrediction {
            val mongoPrediction = MongoPrediction()
            mongoPrediction.timestamp = prediction.timestamp
            mongoPrediction.value = prediction.value
            mongoPrediction.config = MongoPredictionConfig.from(prediction.config)
            mongoPrediction.sensorId = prediction.sensorId
            return mongoPrediction
        }
    }
}

@ApplicationScoped
internal class PanacheMongoPredictionRepository : PanacheMongoRepository<MongoPrediction>
