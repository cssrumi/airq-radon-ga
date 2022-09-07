package pl.airq.ga.radon.adapters.measurement

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoRepository
import io.quarkus.panache.common.Sort
import org.bson.types.ObjectId
import pl.airq.ga.radon.domain.model.Limits
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import pl.airq.ga.radon.infrastructure.mongo.MongoTimeSeries
import pl.airq.ga.radon.infrastructure.mongo.listWithLimit
import pl.airq.ga.radon.infrastructure.util.toStringByReflection
import java.time.Instant
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
internal class MongoMeasurementRepository(
    private val repository: PanacheMongoRepository<MongoMeasurement>
) : MeasurementRepository {

    override fun findAll(id: SensorId, limits: Limits): List<Measurement> {
        val since = limits.sinceLast?.let { Instant.now().minus(it) }
        val results = since?.let { findSince(id, it) } ?: find(id)
        return results.listWithLimit(limits.maxRecords?.toInt()).map { it.toMeasurement() }
    }

    private fun findSince(id: SensorId, since: Instant) = repository
        .find("sensorId = ?1 and timestamp >= ?2", Sort.descending("timestamp"), id.value, since)

    private fun find(id: SensorId) = repository
        .find("sensorId", Sort.descending("timestamp"), id.value)

    override fun save(measurement: Measurement): Measurement = measurement
        .also { repository.persist(MongoMeasurement.from(it)) }

    override fun findLatest(id: SensorId): Measurement? = find(id)
        .firstResult()
        ?.toMeasurement()

}

@MongoEntity(collection = "measurements")
@MongoTimeSeries(timeField = "timestamp", metaField = "sensorId")
class MongoMeasurement {
    var id: ObjectId? = null
    lateinit var sensorId: SensorId
    var radon: Float = 0f
    var pressure: Float = 0f
    lateinit var timestamp: Timestamp

    fun toMeasurement(): Measurement = Measurement(sensorId, radon, pressure, timestamp)

    override fun toString() = toStringByReflection()

    companion object {
        fun from(measurement: Measurement): MongoMeasurement {
            val mongoMeasurement = MongoMeasurement()
            mongoMeasurement.sensorId = measurement.sensorId
            mongoMeasurement.radon = measurement.radon
            mongoMeasurement.pressure = measurement.pressure
            mongoMeasurement.timestamp = measurement.timestamp
            return mongoMeasurement
        }
    }
}

@ApplicationScoped
internal class PanacheMongoMeasurementRepository : PanacheMongoRepository<MongoMeasurement>
