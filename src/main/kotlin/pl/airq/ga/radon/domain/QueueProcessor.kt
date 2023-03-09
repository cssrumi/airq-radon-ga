package pl.airq.ga.radon.domain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.port.UniqueQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


internal abstract class QueueProcessor<T>(
    private val queue: UniqueQueue<T>,
) {
    private val isProcessing = AtomicBoolean(false)
    private val executor = Executors.newSingleThreadExecutor()

    @PostConstruct
    fun start() {
        LOGGER.info("Processor is starting...")
        startExecution()
        queue.registerPutListener { if (!isProcessing.get()) startExecution() }
    }

    private fun startExecution() = executor.submit { processQueue() }

    @PreDestroy
    fun close() = executor.shutdown()

    private fun processQueue() {
        if (isProcessing.compareAndExchange(false, true)) {
            LOGGER.info("Processor already started!")
            return
        }
        LOGGER.info("Processing started.")
        while (!queue.isEmpty()) {
            queue.pop()?.let { processElement(it) }
        }
        LOGGER.info("Processing stopped.")
        isProcessing.set(false)
    }

    abstract fun process(element: T)

    private fun processElement(element: T) {
        try {
            process(element)
        } catch (ex: RuntimeException) {
            LOGGER.error("Processing error: ${ex.message}", ex)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(QueueProcessor::class.java)
    }

}
