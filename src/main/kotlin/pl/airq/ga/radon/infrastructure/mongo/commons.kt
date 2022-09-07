package pl.airq.ga.radon.infrastructure.mongo

import pl.airq.ga.radon.domain.model.PredictionConfig
import pl.airq.ga.radon.infrastructure.util.toStringByReflection
import java.time.temporal.ChronoUnit


class MongoPredictionConfig {
    var timeframe: Long = 0
    lateinit var timeUnit: ChronoUnit
    lateinit var field: String

    fun toPredictionConfig() = PredictionConfig(timeframe, timeUnit, field)

    override fun toString() = toStringByReflection()

    companion object {
        fun from(predictionConfig: PredictionConfig): MongoPredictionConfig {
            val mongoPredictionConfig = MongoPredictionConfig()
            mongoPredictionConfig.field = predictionConfig.field
            mongoPredictionConfig.timeUnit = predictionConfig.timeUnit
            mongoPredictionConfig.timeframe = predictionConfig.timeframe
            return mongoPredictionConfig
        }
    }
}
