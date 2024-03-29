package pl.airq.ga.radon.adapters.evolution

import pl.airq.ga.radon.config.GaProperties
import pl.airq.ga.radon.domain.port.evolution.EvolutionService
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces

@Dependent
class EvolutionConfiguration {

    @Produces
    @ApplicationScoped
    fun jeneticsEvolutionService(gaProperties: GaProperties): EvolutionService {
        val problemEvaluation = JeneticsProblemEvaluation()
        val mapper = JeneticsMapper()
        return JeneticsEvolutionService(problemEvaluation, mapper, gaProperties)
    }

}
