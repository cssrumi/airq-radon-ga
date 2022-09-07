package pl.airq.ga.radon.infrastructure.mongo

import com.google.common.base.Preconditions
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.TimeSeriesOptions
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.runtime.KotlinMongoOperations
import io.quarkus.runtime.StartupEvent
import org.apache.commons.lang3.StringUtils
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.slf4j.LoggerFactory
import pl.airq.ga.radon.domain.model.Timestamp
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Event
import javax.enterprise.event.Observes
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

@ApplicationScoped
internal class MongoTimeSeriesProcessor(private val mongoEntitiesReadyEvent: Event<MongoEntitiesReady>) {
    fun processCollections(@Observes event: StartupEvent?) {
        LOGGER.info("Entities processing started...")
        val config = ConfigurationBuilder()
            .addClassLoaders(Thread.currentThread().contextClassLoader)
            .forPackage("pl.airq.ga.radon")
        val entities = Reflections(config).getTypesAnnotatedWith(MongoTimeSeries::class.java)
        LOGGER.info("Entities found: {}", entities.joinToString(", ") { obj: Class<*> -> obj.name })
        entities.forEach { hasValidAnnotations(it) }
        entities.forEach { hasValidFields(it) }
        entities.forEach { processCollection(it) }
        mongoEntitiesReadyEvent.fire(MongoEntitiesReady())
    }

    private fun hasValidAnnotations(entity: Class<*>) {
        val isValid = entity.isAnnotationPresent(MongoEntity::class.java)
        if (!isValid) {
            LOGGER.error("MongoEntity annotation is missing in {}", entity.name)
            throw UnsupportedOperationException(INVALID_USAGE_MESSAGE)
        }
    }

    private fun hasValidFields(entity: Class<*>) {
        val timeSeries = entity.getAnnotation(MongoTimeSeries::class.java)
        val properties = entity.kotlin.memberProperties
        val timeField = properties.find { it.name == timeSeries.timeField }!!
        Preconditions.checkArgument(timeField.returnType == TIMESTAMP_TYPE, INVALID_USAGE_MESSAGE)
        if (StringUtils.isNotEmpty(timeSeries.metaField)) {
            properties.find { it.name == timeSeries.metaField }!!
        }
    }

    private fun processCollection(entity: Class<*>) {
        val db = KotlinMongoOperations.INSTANCE.mongoDatabase(entity)
        val collectionName = collection(entity)
        val isCollectionCreated = db.listCollectionNames()
            .into(ArrayList())
            .contains(collectionName)
        if (!isCollectionCreated) {
            db.createCollection(collectionName, createCollectionOptions(entity))
            LOGGER.info("Collection {} created", collectionName)
        } else {
            LOGGER.info("Collection {} already created", collectionName)
        }
    }

    private fun createCollectionOptions(entity: Class<*>): CreateCollectionOptions {
        val timeSeries = entity.getAnnotation(MongoTimeSeries::class.java)
        val timeSeriesOptions = TimeSeriesOptions(timeSeries.timeField)
        timeSeriesOptions.granularity(timeSeries.granularity)
        val metaField: String = timeSeries.metaField
        if (StringUtils.isNotEmpty(metaField)) {
            timeSeriesOptions.metaField(metaField)
        }
        val options = CreateCollectionOptions()
        options.timeSeriesOptions(timeSeriesOptions)
        return options
    }

    private fun collection(entity: Class<*>): String {
        val mongoEntity = entity.getAnnotation(MongoEntity::class.java)
        return if (StringUtils.isNotBlank(mongoEntity.collection)) mongoEntity.collection else entity.simpleName
    }

    class MongoEntitiesReady
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MongoTimeSeriesProcessor::class.java)
        private val INVALID_USAGE_MESSAGE = String.format(
            "Invalid usage of %s annotation", MongoTimeSeries::class.java.simpleName
        )
        private val TIMESTAMP_TYPE = Timestamp::class.createType()
    }
}
