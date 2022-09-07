package pl.airq.ga.radon.domain.port.phenotype

import pl.airq.ga.radon.domain.model.phenotype.AirqPhenotype

interface AirqPhenotypeListener {

    fun listen(airqPhenotype: AirqPhenotype)
    fun order(): Int

}
