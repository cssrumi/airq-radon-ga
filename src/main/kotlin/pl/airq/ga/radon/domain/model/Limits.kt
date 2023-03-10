package pl.airq.ga.radon.domain.model

import io.quarkus.runtime.Startup
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.config.GaProperties
import java.time.Duration
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces
import javax.inject.Singleton

data class Limits(
    val maxRecords: Int?,
    val sinceLast: Duration?
) {

    fun isEmpty(): Boolean = this == EMPTY || (this.maxRecords == null && this.sinceLast == null)

    companion object {
        private val EMPTY = Limits(null, null)

        fun from(properties: GaProperties.Limits): Limits {
            val optionalMax = properties.maxRecords()
            val maxRecords = if (optionalMax.isPresent) optionalMax.get() else null
            val sinceLast = Duration.of(properties.sinceLast(), properties.timeUnit())
            return Limits(maxRecords, sinceLast)
        }

        fun empty() = EMPTY
    }
}

@Dependent
class LimitsProducer {

    @Startup
    @Produces
    @Singleton
    fun limits(properties: GaProperties): Limits {
        val limits = properties.limits()
        val produced = Limits.from(limits)
        LOGGER.info("Query limits produced: {}", produced)
        return produced
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LimitsProducer::class.java)
    }
}
