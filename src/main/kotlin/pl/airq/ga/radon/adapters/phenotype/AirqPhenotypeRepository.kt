package pl.airq.ga.radon.adapters.phenotype

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import io.quarkus.panache.common.Sort
import org.bson.types.ObjectId
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import pl.airq.ga.radon.infrastructure.mongo.MongoPredictionConfig
import pl.airq.ga.radon.infrastructure.mongo.MongoTimeSeries
import pl.airq.ga.radon.infrastructure.util.toStringByReflection
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class MongoAirqPhenotypeRepository(
    private val repository: PanacheMongoRepository<MongoAirqPhenotype>
) : AirqPhenotypeRepository {

    override fun save(phenotype: AirqPhenotype) = phenotype
        .also { repository.persist(MongoAirqPhenotype.from(it)) }

    override fun findAll(sensorId: SensorId): Set<AirqPhenotype> = repository
        .find("sensorId", sensorId.value)
        .list()
        .map { it.toAirqPhenotype() }
        .toSet()

    override fun findLatest(sensorId: SensorId): AirqPhenotype? = repository
        .find("sensorId", Sort.descending("timestamp"), sensorId.value)
        .firstResult()
        ?.toAirqPhenotype()

    override fun findBest(sensorId: SensorId, since: Duration?): AirqPhenotype? {
        val query = since?.let { findBestSince(sensorId, it) } ?: findBest(sensorId)
        return query.firstResult()?.toAirqPhenotype()
    }

    private fun findBest(sensorId: SensorId) = repository
        .find("sensorId", Sort.ascending("fitness"), sensorId.value)

    private fun findBestSince(sensorId: SensorId, since: Duration) = repository
        .find("sensorId = ?1 and timestamp >= ?2", Sort.ascending("fitness"), sensorId.value, since)

}

@MongoEntity(collection = "phenotypes")
@MongoTimeSeries(timeField = "timestamp", metaField = "sensorId")
class MongoAirqPhenotype {
    var id: ObjectId? = null
    lateinit var timestamp: Timestamp
    lateinit var sensorId: SensorId
    lateinit var fields: List<String>
    lateinit var values: List<Float>
    lateinit var prediction: MongoPredictionConfig
    var fitness: Double = 0.0

    fun toAirqPhenotype() = AirqPhenotype(timestamp, sensorId, fields, values, prediction.toPredictionConfig(), fitness)

    override fun toString() = toStringByReflection()

    companion object {
        fun from(airqPhenotype: AirqPhenotype): MongoAirqPhenotype {
            val mongoAirqPhenotype = MongoAirqPhenotype()
            mongoAirqPhenotype.timestamp = airqPhenotype.timestamp
            mongoAirqPhenotype.fields = airqPhenotype.fields
            mongoAirqPhenotype.sensorId = airqPhenotype.sensorId
            mongoAirqPhenotype.values = airqPhenotype.values
            mongoAirqPhenotype.prediction = MongoPredictionConfig.from(airqPhenotype.prediction)
            mongoAirqPhenotype.fitness = airqPhenotype.fitness
            return mongoAirqPhenotype
        }
    }
}

@ApplicationScoped
internal class PanacheMongoAirqPhenotypeRepository : PanacheMongoRepository<MongoAirqPhenotype>
