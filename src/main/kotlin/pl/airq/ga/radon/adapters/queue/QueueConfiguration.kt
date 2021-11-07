package pl.airq.ga.radon.adapters.queue

import pl.airq.ga.radon.domain.model.GeneratePhenotypeTask
import pl.airq.ga.radon.domain.port.UniqueQueue
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class QueueConfiguration {

    @Produces
    @Singleton
    fun inMemoryGeneratePhenotypeTaskQueue(): UniqueQueue<GeneratePhenotypeTask> = InMemoryUniqueQueue()
}
