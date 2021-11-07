package pl.airq.ga.radon.domain

import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.OnOverflow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.config.Topics
import pl.airq.ga.radon.domain.event.RadonMeasurementEvent
import pl.airq.ga.radon.domain.port.measurement.MeasurementListener
import java.util.concurrent.CompletionStage
import javax.enterprise.inject.Instance
import javax.inject.Singleton

@Singleton
class RadonMeasurementConsumer(
    listenerInstance: Instance<MeasurementListener>
) {
    private val listeners = listenerInstance.toList().sortedBy { it.order() }

    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = Topics.RADON_MEASUREMENTS_BUFFER_SIZE)
    @Incoming(Topics.RADON_MEASUREMENTS)
    fun consume(message: Message<RadonMeasurementEvent>): CompletionStage<Void> {
        val measurement = message.payload.toMeasurement()
        LOGGER.info("RadonMeasurementEvent for sensor: ${measurement.sensorId.value} consumed")
        listeners.forEach { it.listen(measurement) }
        return message.ack()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RadonMeasurementConsumer::class.java)
    }

}
