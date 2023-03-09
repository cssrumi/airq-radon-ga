package pl.airq.ga.radon.domain.event

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import io.quarkus.runtime.annotations.RegisterForReflection
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RegisterForReflection
open class RadonMeasurementEventDeserializer() :
    ObjectMapperDeserializer<RadonMeasurementEvent>(RadonMeasurementEvent::class.java)

@RegisterForReflection
open class TimestampDeserializer : JsonDeserializer<Timestamp>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): Timestamp = Timestamp(parser.valueAsLong)
}

@RegisterForReflection
open class TimestampIsoDeserializer : JsonDeserializer<Timestamp>() {

    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): Timestamp {
        val instant = Instant.parse(parser.valueAsString)
        return Timestamp.from(instant)
    }

    companion object {
        private val FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        init {
            FORMATTER.withZone(ZoneOffset.UTC)
        }
    }
}

@RegisterForReflection
open class SensorIdDeserializer : JsonDeserializer<SensorId>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): SensorId = SensorId(parser.valueAsString)
}
