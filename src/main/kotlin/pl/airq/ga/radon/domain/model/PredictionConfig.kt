package pl.airq.ga.radon.domain.model

import io.quarkus.runtime.annotations.RegisterForReflection
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.temporal.ChronoUnit

@RegisterForReflection
class PredictionConfig @JsonCreator constructor(
    @param:JsonProperty("timeframe") val timeframe: Long,
    @param:JsonProperty("timeUnit") val timeUnit: ChronoUnit,
    @param:JsonProperty("field") val field: String
)
