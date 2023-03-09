package pl.airq.ga.radon.domain.event

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RadonMeasurementEvent(
    @JsonProperty("GRAFANA_MEASUREMENT")
    val payload: RadonMeasurementPayload?
)
