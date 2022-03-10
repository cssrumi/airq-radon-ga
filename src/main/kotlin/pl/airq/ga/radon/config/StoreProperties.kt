package pl.airq.ga.radon.config

import io.quarkus.runtime.annotations.StaticInitSafe
import io.smallrye.config.ConfigMapping
import javax.validation.constraints.NotNull

@StaticInitSafe
@ConfigMapping(prefix = "store")
interface StoreProperties {
    @NotNull fun type(): StoreType

}

enum class StoreType {
    IN_MEMORY, INFLUX
}
