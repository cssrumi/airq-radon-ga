package pl.airq.ga.radon.infrastructure.util

import io.quarkus.mongodb.panache.common.runtime.CommonPanacheQueryImpl
import io.quarkus.mongodb.panache.kotlin.PanacheQuery
import io.quarkus.mongodb.panache.kotlin.runtime.PanacheQueryImpl
import io.quarkus.runtime.annotations.RegisterForReflection
import org.graalvm.nativeimage.hosted.Feature

@RegisterForReflection(
    targets = [
        Lazy::class,
        PanacheQuery::class,
        PanacheQueryImpl::class,
        CommonPanacheQueryImpl::class
    ]
)
class RegisterThirdPartyClasses : Feature
