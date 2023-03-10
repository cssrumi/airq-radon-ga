package pl.airq.ga.radon.config

import io.quarkus.runtime.annotations.StaticInitSafe
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import java.time.temporal.ChronoUnit
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@StaticInitSafe
@ConfigMapping(prefix = "ga")
interface GaProperties {
    @NotNull fun prediction(): Prediction
    @NotNull fun phenotype(): Phenotype
    @NotNull fun evolution(): Evolution
    @NotNull fun limits(): Limits

    interface Prediction {
        @NotNull fun timeUnit(): ChronoUnit
        @NotNull @Min(1) fun timeFrame(): Long
    }

    interface Phenotype {
        @NotNull fun genotype(): Genotype
        @NotNull @Min(1) @WithDefault("1") fun maximalAge(): Int

        interface Genotype {
            @NotNull fun gene(): Gene

            interface Gene {
                @NotNull fun min(): Int
                @NotNull fun max(): Int
            }
        }
    }

    interface Evolution {
        @NotNull @Positive @WithDefault("1000") fun generations(): Long
        @NotNull @Positive @WithDefault("100") fun populationSize(): Int
    }

    interface Limits {
        fun maxRecords(): Optional<Int>
        @NotNull fun timeUnit(): ChronoUnit
        @NotNull @Min(1) fun sinceLast(): Long
    }

}
