package pl.airq.ga.radon.domain.event

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import io.quarkus.runtime.annotations.RegisterForReflection
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp

@RegisterForReflection
open class RadonMeasurementEventDeserializer() :
    ObjectMapperDeserializer<RadonMeasurementEvent>(RadonMeasurementEvent::class.java)

@RegisterForReflection
open class TimestampDeserializer: JsonDeserializer<Timestamp>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): Timestamp = Timestamp(parser.valueAsLong)
}

@RegisterForReflection
open class SensorIdDeserializer: JsonDeserializer<SensorId>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): SensorId = SensorId(parser.valueAsString)
}
