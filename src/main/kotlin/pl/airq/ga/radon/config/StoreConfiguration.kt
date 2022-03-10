package pl.airq.ga.radon.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.influxdb.LogLevel
import com.influxdb.client.InfluxDBClientOptions
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import com.influxdb.query.FluxRecord
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.runtime.Startup
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.adapters.store.InMemoryStore
import pl.airq.ga.radon.adapters.store.InfluxMapper
import pl.airq.ga.radon.adapters.store.InfluxStore
import pl.airq.ga.radon.domain.model.Measurement
import pl.airq.ga.radon.domain.model.SensorId
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.Store
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Disposes
import javax.enterprise.inject.Produces
import javax.inject.Singleton


@Dependent
@IfBuildProperty(name = "store.type", stringValue = "IN_MEMORY", enableIfMissing = true)
class InMemoryStoreConfiguration {

    @Singleton
    @Produces
    fun inMemoryMeasurementStore(): Store<SensorId, Measurement> = InMemoryStore { _, v -> v.timestamp }

    @Singleton
    @Produces
    fun inMemoryPhenotypeStore(): Store<SensorId, AirqPhenotype> = InMemoryStore { _, v -> v.timestamp }

}

@ApplicationScoped
@IfBuildProperty(name = "store.type", stringValue = "INFLUX", enableIfMissing = false)
class InfluxStoreConfiguration(
    private val properties: InfluxProperties,
    private val mapper: ObjectMapper
) {

    @Singleton
    @Produces
    fun influxMeasurementStore(client: InfluxDBClientKotlin): Store<SensorId, Measurement> {
        return InfluxStore(client, MeasurementInfluxMapper(mapper), "measurements", properties.org().orElse(null))
    }

    @Singleton
    @Produces
    fun influxPhenotypeStore(client: InfluxDBClientKotlin): Store<SensorId, AirqPhenotype> {
        return InfluxStore(client, PhenotypeInfluxMapper(mapper), "phenotypes", properties.org().orElse(null))
    }

    @Startup
    @Singleton
    @Produces
    fun influxClient(properties: InfluxProperties): InfluxDBClientKotlin {
        val builder = InfluxDBClientOptions.builder().url(properties.host()).org(properties.org().orElse(null))
        when (properties.authType()) {
            InfluxAuthType.TOKEN -> builder.authenticateToken(properties.token().get().toCharArray())
            InfluxAuthType.LOGIN ->
                builder.authenticate(properties.username().get(), properties.password().get().toCharArray())
            InfluxAuthType.NONE -> {}
        }
        val client = InfluxDBClientKotlinFactory.create(builder.build())
        client.setLogLevel(properties.logLevel())
        return client
    }

    fun dispose(@Disposes influxClient: InfluxDBClientKotlin) {
        influxClient.close()
        LOGGER.info("Influx client closed.")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InfluxStoreConfiguration::class.java)
    }
}

internal class PhenotypeInfluxMapper(private val mapper: ObjectMapper) :
    InfluxMapper<SensorId, AirqPhenotype>("phenotype", "sensorId") {
    override fun toPoint(key: SensorId, value: AirqPhenotype): Point {
        return Point.measurement(measurementName)
            .addField("fitness", value.fitness)
            .addField("object", mapper.writeValueAsString(value))
            .addTag(keyName, key.value)
            .time(value.timestamp.seconds, WritePrecision.S)
    }

    override fun toValue(record: FluxRecord): AirqPhenotype {
        val rawObject = record.value as String
        return mapper.readValue(rawObject)
    }
}

internal class MeasurementInfluxMapper(private val mapper: ObjectMapper) :
    InfluxMapper<SensorId, Measurement>("measurement", "sensorId") {
    override fun toPoint(key: SensorId, value: Measurement): Point {
        return Point.measurement(measurementName)
            .addField("object", mapper.writeValueAsString(value))
            .addTag(keyName, key.value)
            .time(value.timestamp.seconds, WritePrecision.S)
    }

    override fun toValue(record: FluxRecord): Measurement {
        val rawObject = record.value as String
        return mapper.readValue(rawObject)
    }
}

@Startup
@Singleton
class StoreLogger(private val properties: StoreProperties) {

    @PostConstruct
    fun postConstruct() {
        LOGGER.info("Selected store type: ${properties.type()}")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoreLogger::class.java)
    }
}
