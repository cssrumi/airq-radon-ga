package pl.airq.ga.radon.config

import com.influxdb.LogLevel
import io.quarkus.arc.properties.IfBuildProperty
import io.quarkus.runtime.annotations.StaticInitSafe
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import java.util.*
import javax.annotation.Nullable
import javax.validation.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import kotlin.reflect.KClass


@ConfigMapping(prefix = "influx")
@IfBuildProperty(name = "store.type", stringValue = "INFLUX", enableIfMissing = false)
@StaticInitSafe
@InfluxPropertiesValid
interface InfluxProperties {
    @NotNull @NotEmpty @WithDefault("http://localhost:8086") fun host(): String
    fun token(): Optional<String>
    fun org(): Optional<String>
    fun username(): Optional<String>
    fun password(): Optional<String>
    @NotNull @WithDefault("NONE") fun logLevel(): LogLevel
    @NotNull @WithDefault("NONE") fun authType(): InfluxAuthType

}

enum class InfluxAuthType {
    TOKEN, LOGIN, NONE
}

@Constraint(validatedBy = [InfluxPropertiesValidator::class])
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class InfluxPropertiesValid(
    val message: String = "Invalid influx properties",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class InfluxPropertiesValidator : ConstraintValidator<InfluxPropertiesValid, InfluxProperties> {

    override fun isValid(influxProperties: InfluxProperties?, context: ConstraintValidatorContext?): Boolean {
        return influxProperties?.let {
            when (it.authType()) {
                InfluxAuthType.LOGIN -> it.username().isPresent && it.password().isPresent
                InfluxAuthType.TOKEN -> it.token().isPresent
                InfluxAuthType.NONE -> true
            }
        } ?: true
    }

}
