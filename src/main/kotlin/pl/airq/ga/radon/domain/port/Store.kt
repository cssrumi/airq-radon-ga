package pl.airq.ga.radon.domain.port

interface Store<K, V> {

    fun put(key: K, value: V)
    fun get(key: K): V?
    fun getAll(): List<V>

}
