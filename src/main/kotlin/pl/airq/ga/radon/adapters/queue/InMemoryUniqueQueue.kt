package pl.airq.ga.radon.adapters.queue

import pl.airq.ga.radon.domain.port.UniqueQueue
import java.util.*
import javax.inject.Singleton

@Singleton
internal class InMemoryUniqueQueue<T> : UniqueQueue<T> {

    private val entrySet = mutableSetOf<T>()
    private val queue: MutableList<T> = LinkedList()
    private val listeners = mutableSetOf<(T) -> Unit>()

    @Synchronized
    override fun put(value: T) {
        if (entrySet.contains(value)) { return }
        entrySet.add(value)
        queue.add(value)
        listeners.forEach { it.invoke(value) }
    }

    override fun pop(): T? = queue.removeFirstOrNull()?.also { entrySet.remove(it) }
    override fun size(): Int = queue.size
    override fun isEmpty(): Boolean = queue.isEmpty()
    override fun registerPutListener(listener: (T) -> Unit) = listeners.add(listener).let { }

}
