package pl.airq.ga.radon.domain.event

import io.smallrye.common.annotation.Identifier
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler
import org.apache.kafka.common.header.Headers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
@Identifier("LogAndSkipDeserializationFailureHandler")
class LogAndSkipDeserializationFailureHandler : DeserializationFailureHandler<Any> {

    override fun handleDeserializationFailure(
        topic: String,
        isKey: Boolean,
        deserializer: String?,
        data: ByteArray?,
        exception: Exception,
        headers: Headers?
    ): Any? {
        LOGGER.error(
            "Unable to deserialize event on topic: $topic. Is key: $isKey. Reason: ${exception.message}",
            exception
        )
        return null
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LogAndSkipDeserializationFailureHandler::class.java)
    }
}
