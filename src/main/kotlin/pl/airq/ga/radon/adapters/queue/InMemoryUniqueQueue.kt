package pl.airq.ga.radon.adapters.queue

import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.port.UniqueQueue

internal class InMemoryUniqueQueue<T>(
    private val name: String
) : UniqueQueue<T> {

    private val queue: LinkedHashSet<T> = LinkedHashSet()
    private val listeners = mutableSetOf<(T) -> Unit>()

    @Synchronized
    override fun put(value: T) {
        queue.add(value)
        listeners.forEach { it.invoke(value) }
    }

    @Synchronized
    override fun pop(): T? = queue.firstOrNull()?.also { queue.remove(it) }
    override fun size(): Int = queue.size
    override fun isEmpty(): Boolean = queue.isEmpty()
    override fun registerPutListener(listener: (T) -> Unit) = listeners.add(listener).let { }
    override fun name(): String = name

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InMemoryUniqueQueue::class.java)
    }
}
