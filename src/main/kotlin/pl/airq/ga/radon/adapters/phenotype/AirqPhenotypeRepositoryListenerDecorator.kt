package pl.airq.ga.radon.adapters.phenotype

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeListener
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeRepository
import java.time.Duration
import javax.annotation.Priority
import javax.decorator.Decorator
import javax.decorator.Delegate
import javax.enterprise.inject.Any
import javax.enterprise.inject.Instance

@Priority(Int.MAX_VALUE)
@Decorator
class AirqPhenotypeRepositoryListenerDecorator(
    @Any @Delegate private val delegate: AirqPhenotypeRepository,
    listenerInstance: Instance<AirqPhenotypeListener>
) : AirqPhenotypeRepository {
    private val listeners = listenerInstance.toList().sortedBy { it.order() }

    override fun save(phenotype: AirqPhenotype): AirqPhenotype {
        val result = delegate.save(phenotype)
        LOGGER.debug("Invoking phenotype listeners...")
        listeners.forEach { it.listen(phenotype) }
        return result
    }

    override fun findAll(sensorId: SensorId): Set<AirqPhenotype> = delegate.findAll(sensorId)
    override fun findLatest(sensorId: SensorId): AirqPhenotype? = delegate.findLatest(sensorId)
    override fun findBest(sensorId: SensorId, since: Duration?): AirqPhenotype? = delegate.findBest(sensorId, since)

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AirqPhenotypeRepositoryListenerDecorator::class.java)
    }
}
