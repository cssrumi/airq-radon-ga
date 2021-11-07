package pl.airq.ga.radon.adapters.measurement

import pl.airq.ga.radon.adapters.store.InMemoryStore
import pl.airq.ga.radon.domain.model.GeneratePhenotypeTask
import pl.airq.ga.radon.domain.port.UniqueQueue
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import pl.airq.ga.radon.domain.port.measurement.MeasurementRepository
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

@Dependent
class MeasurementConfiguration {

    @Produces
    @ApplicationScoped
    fun measurementRepository(): MeasurementRepository = StoreMeasurementRepository(InMemoryStore())

    @Produces
    @Singleton
    fun phenotypeTaskCreatorMeasurementListener(taskQueue: UniqueQueue<GeneratePhenotypeTask>)
    : MeasurementListener = PhenotypeTaskCreatorMeasurementListener(taskQueue)

    @Produces
    @Singleton
    fun saveMeasurementListener(measurementRepository: MeasurementRepository)
    : MeasurementListener = SaveMeasurementListener(measurementRepository)
}
