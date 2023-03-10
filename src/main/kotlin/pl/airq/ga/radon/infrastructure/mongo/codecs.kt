package pl.airq.ga.radon.infrastructure.mongo

import io.quarkus.runtime.annotations.RegisterForReflection
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.StringCodec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.jsr310.InstantCodec
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.Timestamp
import javax.inject.Singleton

@RegisterForReflection
open class SensorIdCodec : Codec<SensorId> {
    private val stringCodec = StringCodec()

    override fun encode(writer: BsonWriter, sensorId: SensorId, encoderContext: EncoderContext) {
        stringCodec.encode(writer, sensorId.value, encoderContext)
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): SensorId {
        val value = stringCodec.decode(reader, decoderContext)
        return SensorId(value)
    }

    override fun getEncoderClass() = SensorId::class.java
}

@Singleton
open class SensorIdCodecProvider : CodecProvider {
    override fun <T> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        return if (clazz == SensorId::class.java) SensorIdCodec() as Codec<T> else null
    }
}

@RegisterForReflection
open class TimestampCodec : Codec<Timestamp> {
    private val instantCodec = InstantCodec()

    override fun encode(writer: BsonWriter, timestamp: Timestamp, encoderContext: EncoderContext) {
        instantCodec.encode(writer, timestamp.toInstant(), encoderContext)
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): Timestamp {
        val instant = instantCodec.decode(reader, decoderContext)
        return Timestamp.from(instant)
    }

    override fun getEncoderClass() = Timestamp::class.java
}

@Singleton
open class TimestampCodecProvider : CodecProvider {
    override fun <T> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        return if (clazz == Timestamp::class.java) TimestampCodec() as Codec<T> else null
    }
}
