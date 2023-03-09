package pl.airq.ga.radon.domain

import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.OnOverflow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.config.Topics
import pl.airq.ga.radon.domain.event.RadonMeasurementEvent
import pl.airq.ga.radon.domain.event.RadonMeasurementPayload
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import java.util.concurrent.CompletionStage
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance

@ApplicationScoped
class RadonMeasurementConsumer(
    listenerInstance: Instance<MeasurementListener>
) {
    private val listeners = listenerInstance.toList().sortedBy { it.order() }

    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = Topics.RADON_MEASUREMENTS_BUFFER_SIZE)
    @Incoming(Topics.RADON_MEASUREMENTS)
    fun consume(message: Message<RadonMeasurementEvent>): CompletionStage<Void> {
        message.payload.payload?.let { processPayload(it) }
            ?: LOGGER.info("Unhandled measurement event...")
        return message.ack()
    }

    private fun processPayload(payload: RadonMeasurementPayload) {
        val measurement = payload.toMeasurement()
        LOGGER.info("RadonMeasurementEvent for sensor: ${measurement.sensorId.value} consumed")
        listeners.forEach { it.listen(measurement) }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RadonMeasurementConsumer::class.java)
    }

}
