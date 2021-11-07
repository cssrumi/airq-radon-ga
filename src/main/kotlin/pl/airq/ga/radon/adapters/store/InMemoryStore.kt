package pl.airq.ga.radon.adapters.store

import pl.airq.ga.radon.domain.port.Store
import java.util.concurrent.ConcurrentHashMap

class InMemoryStore<K, V> : Store<K, V> {

    private val db = ConcurrentHashMap<K, V>()

    override fun put(key: K, value: V) {
        db[key] = value
    }

    override fun get(key: K): V? = db[key]
    override fun getAll(): List<V> = db.values.toList()
}
