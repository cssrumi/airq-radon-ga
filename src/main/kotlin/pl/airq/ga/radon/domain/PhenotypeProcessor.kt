package pl.airq.ga.radon.domain

import io.quarkus.runtime.Startup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.GeneratePhenotypeTask
import pl.airq.ga.radon.domain.port.UniqueQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Singleton

@Startup
@Singleton
class PhenotypeProcessor(
    private val queue: UniqueQueue<GeneratePhenotypeTask>,
    private val evolutionServiceFacade: EvolutionServiceFacade
) {
    private val isProcessing = AtomicBoolean(false)
    private val executor = Executors.newSingleThreadExecutor()

    @PostConstruct
    fun start() {
        LOGGER.info("Phenotype processor is starting...")
        startExecution()
        queue.registerPutListener { if (!isProcessing.get()) startExecution() }
    }

    private fun startExecution() = executor.submit { processQueue() }

    @PreDestroy
    fun close() { executor.shutdown() }

    private fun processQueue() {
        if (isProcessing.compareAndExchange(false, true)) {
            LOGGER.info("Phenotype processor already started!")
            return
        }
        LOGGER.info("Phenotype processing started.")
        while (!queue.isEmpty()) {
            queue.pop()?.let { process(it) }
        }
        LOGGER.info("Phenotype processing stopped.")
        isProcessing.set(false)
    }

    private fun process(task: GeneratePhenotypeTask) {
        try {
            evolutionServiceFacade.generateNewPhenotype(task.sensorId)
        } catch (ex: RuntimeException) {
            LOGGER.error("Phenotype processing error: ${ex.message}", ex)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PhenotypeProcessor::class.java)
    }

}
