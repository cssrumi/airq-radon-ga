package pl.airq.ga.radon.domain.port

interface UniqueQueue<T> {

    fun put(value: T)
    fun pop(): T?
    fun size(): Int
    fun isEmpty(): Boolean
    fun registerPutListener(listener: (T) -> Unit)
    fun name(): String

}
