package pl.airq.ga.radon.domain.port

import pl.airq.ga.radon.domain.model.Key
import java.time.Duration

interface Store<K : Key, V> {

    fun put(key: K, value: V)
    fun get(key: K): V?
    fun getAll(key: K): List<V>
    fun getAll(key: K, sinceLast: Duration): List<V>
    fun close() {}

}
