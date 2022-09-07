package pl.airq.ga.radon.adapters.prediction

import pl.airq.ga.radon.domain.PredictionService
import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype
import pl.airq.ga.radon.domain.port.phenotype.AirqPhenotypeListener
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PredictionAirqPhenotypeListener(
    private val predictionService: PredictionService
) : AirqPhenotypeListener {

    override fun listen(airqPhenotype: AirqPhenotype) {
        predictionService.predict(airqPhenotype.sensorId)
    }

    override fun order() = 0
}
