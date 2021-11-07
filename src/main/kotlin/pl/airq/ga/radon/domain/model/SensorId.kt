package pl.airq.ga.radon.domain.model

data class SensorId(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "StationId value can't be null"
        }
    }
}
