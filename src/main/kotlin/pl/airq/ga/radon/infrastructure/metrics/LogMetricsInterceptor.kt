package pl.airq.ga.radon.infrastructure.metrics

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import javax.annotation.Priority
import javax.interceptor.AroundInvoke
import javax.interceptor.Interceptor
import javax.interceptor.InvocationContext

@LogMetrics
@Priority(Int.MAX_VALUE)
@Interceptor
class LogMetricsInterceptor {

    @AroundInvoke
    fun invoke(context: InvocationContext): Any? {
        val start = Instant.now()
        val annotation = context.method.getAnnotation(LogMetrics::class.java)
        val methodName = annotation.named.ifEmpty { context.method.name }
        val proceed = context.proceed()
        val proceedInSeconds = Duration.between(start, Instant.now()).seconds
        LOGGER.info("{} proceed in {} s", methodName, proceedInSeconds)
        return proceed
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LogMetricsInterceptor::class.java)
    }
}
