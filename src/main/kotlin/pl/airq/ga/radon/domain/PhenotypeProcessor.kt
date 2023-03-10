package pl.airq.ga.radon.domain

import io.quarkus.runtime.Startup
import pl.airq.ga.radon.domain.model.GeneratePhenotypeTask
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.port.UniqueQueue
import javax.inject.Singleton

@Startup
@Singleton
private class PhenotypeProcessor(
    queue: UniqueQueue<GeneratePhenotypeTask>,
    private val evolutionServiceFacade: EvolutionServiceFacade
) : QueueProcessor<GeneratePhenotypeTask>(queue) {

    override fun process(element: GeneratePhenotypeTask) {
        evolutionServiceFacade.generateNewPhenotype(element.sensorId)
    }

    override fun sensorId(element: GeneratePhenotypeTask): SensorId = element.sensorId
}
