package pl.airq.ga.radon.adapters.store

import pl.airq.ga.radon.domain.model.Key
import pl.airq.ga.radon.domain.model.Timestamp
import pl.airq.ga.radon.domain.port.Store
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class InMemoryStore<K : Key, V>(
    private val mapper: TimestampMapper<K, V> = TimestampMapper { _, _ -> Timestamp.now() }
) : Store<K, V> {

    private val db = ConcurrentHashMap<K, MutableSet<TimestampedValue<V>>>()

    override fun put(key: K, value: V) {
        val current = db[key] ?: mutableSetOf()
        current.add(TimestampedValue(value, mapper.timestamp(key, value)))
        db[key] = current
    }

    override fun get(key: K): V? = db[key]?.last()?.value
    override fun getAll(key: K): List<V> = db[key]?.map { it.value }?.toList() ?: listOf()
    override fun getAll(key: K, sinceLast: Duration): List<V> {
        val oldestInstant = Instant.now().minus(sinceLast)
        return db[key]?.filter { oldestInstant.isBefore(it.timestamp.toInstant()) }
            ?.map { it.value }?.toList() ?: listOf()
    }

    data class TimestampedValue<V>(val value: V, val timestamp: Timestamp)

}

fun interface TimestampMapper<K, V> {
    fun timestamp(key: K, value: V): Timestamp
}
