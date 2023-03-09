package pl.airq.ga.radon.domain

import io.quarkus.runtime.Startup
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.port.UniqueQueue
import javax.inject.Singleton

@Startup
@Singleton
private class PredictionProcessor(
    queue: UniqueQueue<SensorId>,
    private val predictionService: PredictionService
) : QueueProcessor<SensorId>(queue) {

    override fun process(element: SensorId) {
        predictionService.predict(element)
    }
}
