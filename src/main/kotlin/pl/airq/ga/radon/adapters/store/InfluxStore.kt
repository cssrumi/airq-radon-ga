package pl.airq.ga.radon.adapters.store

import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.write.Point
import com.influxdb.query.FluxRecord
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.Key
import pl.airq.ga.radon.domain.port.Store
import java.time.Duration
import java.time.temporal.ChronoUnit

class InfluxStore<K : Key, V>(
    private val client: InfluxDBClientKotlin,
    private val mapper: InfluxMapper<K, V>,
    private val bucket: String,
    private val org: String?
) : Store<K, V> {

    override fun put(key: K, value: V) {
        LOGGER.info("Put: {} for {}", value, key.value())
        runBlocking {
            client.getWriteKotlinApi().writePoint(mapper.toPoint(key, value), bucket, org)
        }
    }

    override fun get(key: K): V? {
        LOGGER.info("Get for {}", key.value())
        return runBlocking {
            client.getQueryKotlinApi()
                .query(
                    """from(bucket:"$bucket")
                            |> filter(fn: (r) =>
                                r._measurement == "${mapper.measurementName}" 
                                and r.${mapper.keyName} == "${key.value()}")
                            |> last()"""
                )
                .consumeAsFlow()
                .map { mapper.toValue(it) }
                .lastOrNull()
        }
    }

    override fun getAll(key: K): List<V> {
        LOGGER.info("Get all for {}", key.value())
        return runBlocking {
            client.getQueryKotlinApi()
                .query(
                    """from(bucket: "$bucket")
                            |> range(start: 0, stop: now())
                            |> filter(fn: (r) =>
                                r._measurement == "${mapper.measurementName}"
                                and r.${mapper.keyName} == "${key.value()}"
                                and r._field == "object"
                            )"""
                )
                .consumeAsFlow()
                .map { mapper.toValue(it) }
                .toList()
        }
    }

    override fun getAll(key: K, sinceLast: Duration): List<V> {
        LOGGER.info("Get all for {} since last {}", key.value(), sinceLast)
        return runBlocking {
            client.getQueryKotlinApi()
                .query(
                    """from(bucket: "$bucket") 
                            |> range(start: -${sinceLast.toMinutes()}m)
                            |> filter(fn: (r) =>
                                r._measurement == "${mapper.measurementName}"
                                and r.${mapper.keyName} == "${key.value()}"
                                and r._field == "object"
                            )"""
                )
                .consumeAsFlow()
                .map { mapper.toValue(it) }
                .toList()
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(InfluxStore::class.java)
    }

}

abstract class InfluxMapper<K : Key, T>(val measurementName: String, val keyName: String) {
    abstract fun toPoint(key: K, value: T): Point
    abstract fun toValue(record: FluxRecord): T
}
