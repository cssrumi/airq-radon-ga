package pl.airq.ga.radon.infrastructure.mongo;

import com.mongodb.client.model.TimeSeriesGranularity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RegisterForReflection
public @interface MongoTimeSeries {

    String timeField();
    String metaField() default "";
    TimeSeriesGranularity granularity() default TimeSeriesGranularity.MINUTES;

}
