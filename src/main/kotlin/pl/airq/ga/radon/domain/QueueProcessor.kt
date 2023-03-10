package pl.airq.ga.radon.domain

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.port.UniqueQueue
import pl.airq.ga.radon.infrastructure.LoggingConstants
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


internal abstract class QueueProcessor<T>(
    private val queue: UniqueQueue<T>,
) {
    private val isProcessing = AtomicBoolean(false)
    private val executor = singleTaskExecutorService()

    @PostConstruct
    fun start() {
        LOGGER.info("{} processor is starting...", queue.name())
        if (!queue.isEmpty()) startExecution()
        queue.registerPutListener { if (!isProcessing.get()) startExecution() }
    }

    private fun startExecution() = executor.submit { processQueue() }

    @PreDestroy
    fun close() = executor.shutdown()

    private fun processQueue() {
        if (isProcessing.compareAndExchange(false, true)) {
            LOGGER.info("{} processor already started!", queue.name())
            return
        }
        LOGGER.info("{} processing started.", queue.name())
        while (!queue.isEmpty()) {
            queue.pop()?.let { processElement(it) }
        }
        LOGGER.info("Processing stopped.")
        isProcessing.set(false)
    }

    abstract fun process(element: T)
    abstract fun sensorId(element: T): SensorId

    private fun processElement(element: T) {
        try {
            MDC.put(LoggingConstants.SENSOR_ID, sensorId(element).value)
            process(element)
        } catch (ex: RuntimeException) {
            LOGGER.error("{} processing error: {}", queue.name(), ex.message, ex)
        } finally {
            MDC.remove(LoggingConstants.SENSOR_ID)
        }
    }

    private fun singleTaskExecutorService(): ExecutorService {
        return ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MINUTES, LinkedBlockingQueue(1),
            namedThreadFactory(), ThreadPoolExecutor.DiscardPolicy()
        )
    }

    private fun namedThreadFactory(): ThreadFactory {
        return ThreadFactoryBuilder().setNameFormat("${queue.name().lowercase()}-processor").build()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(QueueProcessor::class.java)
    }

}
